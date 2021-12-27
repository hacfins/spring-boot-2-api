package com.langyastudio.edu.admin.service.impl;

import com.langyastudio.edu.admin.common.data.Define;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.admin.service.AuthService;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.data.PageIn;
import com.langyastudio.edu.common.data.PageInfo;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.util.Tool;
import com.langyastudio.edu.db.mapper.*;
import com.langyastudio.edu.db.model.UmsApi;
import com.langyastudio.edu.db.model.UmsRoleApis;
import com.langyastudio.edu.db.model.UmsUser;
import com.langyastudio.edu.db.model.UmsUserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AuthServiceImpl extends BaseServiceImpl implements AuthService
{
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UmsUserMapper umsUserMapper;

    @Autowired
    UmsUserAuthMapper umsUserAuthMapper;

    @Autowired
    UmsUserRolesMapper umsUserRolesMapper;

    @Autowired
    UmsRoleApisMapper umsRoleApisMapper;

    @Autowired
    UmsApiMapper umsApiMapper;

    @Autowired
    UmsRoleMapper umsRoleMapper;

    /*-------------------------------------------------------------------------------------------------------------- */
    // 权限管理
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 根据角色Id号获取API权限列表
     *
     * @param roleId 角色Id号
     *
     * @return
     */
    @Override
    public PageInfo<UmsApi> getAuthListByRoleId(String roleId)
    {
        //1.0 判断角色是否存在
        if (null == umsRoleMapper.getInfoByRoleId(roleId))
        {
            throw new MyException(EC.ERROR_ROLE_NOT_EXIST);
        }

        //2.0 获取权限列表
        List<String> apiIds    = umsRoleApisMapper.getApiIdsByRoleId(List.of(roleId));
        List<String> apiIdsAll = umsApiMapper.getApiIdsAll();

        //填充所有权限
        UmsApi              umsApiValue;
        Map<String, UmsApi> apiMapList = new HashMap<>();
        for (Iterator<String> iterator = apiIdsAll.iterator(); iterator.hasNext(); )
        {
            String apiId = iterator.next();
            umsApiValue = umsApiMapper.getInfoByApiId(apiId);
            if (null != umsApiValue)
            {
                umsApiValue.setStatus(Define.NOI);
                apiMapList.put(apiId, umsApiValue);
            }
        }
        //标记有效权限
        if (null != apiIds)
        {
            for (Iterator<String> iterator = apiIds.iterator(); iterator.hasNext(); )
            {
                String apiId = iterator.next();
                umsApiValue = apiMapList.get(apiId);
                if (null != umsApiValue)
                {
                    umsApiValue.setStatus(Define.YESI);
                    apiMapList.put(apiId, umsApiValue);
                }
            }
        }

        return new PageInfo<UmsApi>((long) apiMapList.size(), new ArrayList<UmsApi>(apiMapList.values()));
    }

    /**
     * 设置角色映射的API权限列表
     *
     * @param roleId       角色Id号
     * @param desStrApiIds API权限Id号列表
     *
     * @return
     */
    @Override
    @Transactional
    public Integer setAuthByRoleId(String roleId, String desStrApiIds)
    {
        //1.0 判断角色是否存在
        if (null == umsRoleMapper.getInfoByRoleId(roleId))
        {
            throw new MyException(EC.ERROR_ROLE_NOT_EXIST);
        }

        List<String> desApiIds = null;
        if (null != desStrApiIds)
        {
            desApiIds = Arrays.asList(desStrApiIds.split("\\|"));
        }

        //2.0 获取权限列表
        List<String> sorApiIds = umsRoleApisMapper.getApiIdsByRoleId(List.of(roleId));

        //2.1 移除的Id号列表
        if (null != sorApiIds && sorApiIds.size() > 0)
        {
            List<String> delApiIds = Tool.delArrayBat(sorApiIds, desApiIds);
            if (delApiIds.size() > 0)
            {
                umsRoleApisMapper.batchDeleteByRoleId(roleId, delApiIds);
            }
        }

        //2.2 新增的Id号列表
        if (null != desApiIds)
        {
            List<String> insertApiIds = Tool.delArrayBat(desApiIds, sorApiIds);
            if (insertApiIds.size() > 0)
            {
                List<UmsRoleApis> umsRoleApis = new ArrayList<>();
                for (String insertApiId : insertApiIds)
                {
                    umsRoleApis.add(new UmsRoleApis(null, roleId, insertApiId, null, null, null));
                }
                umsRoleApisMapper.batchInsertRoleApis(umsRoleApis);
            }
        }

        return Define.YESI;
    }

    /*-------------------------------------------------------------------------------------------------------------- */
    // 用户管理
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 系统用户列表
     *
     * @param roleId      角色Id号
     * @param userNameKey 用户名关键字
     * @param phoneKey    电话号码关键字
     * @param fullNameKey 姓名关键字
     * @param isAsc       是否升序
     * @param offset      页码
     * @param pageSize    页数
     *
     * @return
     */
    @Override
    public PageInfo<Map<String, Object>> getUserList(String roleId, String userNameKey, String fullNameKey,
                                                     String phoneKey,
                                                     Integer isAsc, Integer offset, Integer pageSize)
    {
        //1.0 判断角色是否存在
        if (StrUtil.isNotBlank(roleId) && null == umsRoleMapper.getInfoByRoleId(roleId))
        {
            throw new MyException(EC.ERROR_ROLE_NOT_EXIST);
        }

        //2.0 获取用户名列表
        PageIn<String> pageIns = new PageIn<>(offset, pageSize);
        umsUserMapper.getUserNameList(roleId, userNameKey, fullNameKey, phoneKey, UmsUser.USER_TYPE_USER, isAsc,
                                      pageIns);
        List<Map<String, Object>> resultLists = new ArrayList<>();

        //循环获取记录列表
        long total = pageIns.getTotal();
        if (total > 0)
        {
            Map<String, Object> result      = null;
            for (String userName : pageIns.getRecords())
            {
                result = this.getSystemUserInfoDetail(userName);
                if (null != result)
                {
                    resultLists.add(result);
                }
            }
        }

        PageInfo<Map<String, Object>> pageInfoRole = new PageInfo<>();
        pageInfoRole.setTotal(total);
        pageInfoRole.setList(resultLists);

        return pageInfoRole;
    }

    /**
     * 设置用户映射的角色
     *
     * @param userName 用户名
     * @param roleIds  角色Id号列表
     * @param schoolId 机构Id号
     *
     * @return
     */
    @Override
    @Transactional
    public Integer setUserRoles(String userName, String roleIds, String schoolId)
    {
        //1.0 表示系统设置
        if (null == schoolId)
        {
            schoolId = Define.SHOOL_SYSTEM_ID;
        }
        else
        //机构是否有效
        {

        }

        //1.1 用户名校验
        if (Objects.isNull(umsUserMapper.getInfoByUserName(userName)))
        {
            throw new MyException(EC.ERROR_USER_NOT_EXIST);
        }

        //1.2 修改角色
        return this.modifyUserRoleIds(userName, roleIds, schoolId, true, true);
    }

    /**
     * 修改系统用户
     *
     * @param userName 用户名
     * @param fullName 姓名
     * @param phone    手机号
     * @param sex      性别
     *
     * @return
     */
    @Override
    @Transactional
    public Integer modifyUser(String userName, String fullName, String phone, Integer sex)
    {
        //1.1 用户名是否存在
        UmsUser umsUserInfo = umsUserMapper.getInfoByUserName(userName);
        if (null == umsUserInfo)
        {
            throw new MyException(EC.ERROR_USER_NOT_EXIST);
        }

        //1.2 手机号是否存在
        if (!StrUtil.isBlank(phone))
        {
            String userNamePhone = umsUserAuthMapper.getUserNameByPhone(phone);
            if (!StrUtil.isEmptyIfStr(userNamePhone) && !userName.equals(userNamePhone))
            {
                throw new MyException(EC.ERROR_USER_PHONE_EXIST);
            }
        }

        //2.1 修改用户信息
        umsUserInfo.setFullName(fullName);
        umsUserInfo.setSex(Convert.toByte(sex));
        umsUserMapper.updateByUserName(umsUserInfo);

        //修改Auth
        UmsUserAuth umsUserAuthInfo = umsUserAuthMapper.getInfoByUserName(userName);
        umsUserAuthInfo.setPhone(phone);
        umsUserAuthMapper.updateByUserName(umsUserAuthInfo);

        return Define.YESI;
    }

    /**
     * 重置密码
     *
     * @param userName 用户名
     *
     * @return
     */
    @Override
    public Integer resetPwd(String userName)
    {
        //1.1 用户名是否存在
        UmsUserAuth umsUserAuthInfo = umsUserAuthMapper.getInfoByUserName(userName);
        if (null == umsUserAuthInfo)
        {
            throw new MyException(EC.ERROR_USER_NOT_EXIST);
        }

        //2.1 修改密码为123456a
        umsUserAuthInfo.setPwd(passwordEncoder.encode(UmsUserAuth.USER_AUTH_PWD));
        umsUserAuthMapper.updateByUserName(umsUserAuthInfo);

        return Define.YESI;
    }
}
