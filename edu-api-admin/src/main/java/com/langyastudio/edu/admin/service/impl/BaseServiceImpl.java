package com.langyastudio.edu.admin.service.impl;

import com.langyastudio.edu.admin.common.data.Define;
import com.langyastudio.edu.admin.service.base.BaseCacheService;
import com.langyastudio.edu.common.util.RedisT;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.admin.service.BaseService;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.util.Tool;
import com.langyastudio.edu.db.mapper.*;
import com.langyastudio.edu.db.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 基类服务层
 */
@Log4j2
public class BaseServiceImpl implements BaseService
{
    static final String CACHE_PRE = "edu_baseservice:";

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
    UmsApiMapper      umsApiMapper;
    @Autowired
    UmsRoleMapper     umsRoleMapper;
    @Autowired
    UmsUserLoginLogsMapper umsUserLoginLogsMapper;

    @Autowired
    UmsDivisionMapper umsDivisionMapper;

    @Autowired
    BaseCacheService baseCacheService;

    @Autowired
    RedisT redisT;

    /*-------------------------------------------------------------------------------------------------------------- */
    // 登录认证
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 身份识别
     * 也可以在Controller层注入Principal
     *
     * @param bNeedLogin 是否需要登录
     *
     * @return 用户名
     */
    @Override
    public String getUserName(boolean bNeedLogin)
    {
        String userName = null;

        // 获取用户认证信息对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 认证信息可能为空，因此需要进行判断
        if (Objects.nonNull(authentication))
        {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails)
            {
                userName = ((UserDetails) principal).getUsername();
            }
        }

        if (bNeedLogin && StrUtil.isBlankIfStr(userName))
        {
            throw new MyException(EC.ERROR_USER_UNAUTHORIZED);
        }

        return userName;
    }

    /*-------------------------------------------------------------------------------------------------------------- */
    // 用户操作
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 添加系统用户
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
    public Map<String, Object> addSystemUser(String userName, String pwd,
                                             String fullName, String phone, Integer sex, Byte userType,
                                             Integer checkUserName
    )
    {
        //1.0 生成自定义用户名
        if (StrUtil.isEmptyIfStr(userName))
        {
            //手机号后四位
            String end = RandomUtil.randomNumbers(4);
            if (!StrUtil.isEmptyIfStr(phone))
            {
                end = phone.substring(phone.length() - 4, phone.length());
            }

            //取一个未用的用户名
            userName = "a" + end;
            int count = 10;
            while ((count-- > 0) && null != umsUserMapper.getInfoByUserName(userName))
            {
                userName = "a" + RandomUtil.randomNumbers(2) + end;
            }
        }
        else
        {
            if (userName.length() > 20 || userName.length() < 2)
            {
                throw new MyException(EC.ERROR_USER_PARAM_ERROR);
            }

            if (checkUserName.equals(Define.YESI))
            {
                if (!userName.matches(Define.PATTERN_USER_NAME))
                {
                    throw new MyException(EC.ERROR_USER_PARAM_ERROR);
                }
            }
        }

        if (StrUtil.isNotBlank(fullName) && (fullName.length() > 20 || fullName.length() < 2))
        {
            throw new MyException(EC.ERROR_USER_FULLNAME_ERROR);
        }

        //1.1 用户名是否存在
        if (null != umsUserMapper.getInfoByUserName(userName))
        {
            throw new MyException(EC.ERROR_USER_NAME_EXIST);
        }

        //1.2 手机号是否存在
        if (!StrUtil.isEmptyIfStr(phone))
        {
            if (!phone.matches(Define.PATTERN_PHONE))
            {
                throw new MyException(EC.ERROR_USER_PHONE_PARAM_ERROR);
            }

            if (null != umsUserAuthMapper.getUserNameByPhone(phone))
            {
                throw new MyException(EC.ERROR_USER_PHONE_EXIST);
            }
        }

        if (Objects.isNull(pwd))
        {
            pwd = UmsUserAuth.USER_AUTH_PWD;
        }

        //2.1 写入数据
        //用户
        UmsUser umsUserBean = new UmsUser();
        String  strName     = StrUtil.isEmptyIfStr(fullName) ? userName : fullName;
        umsUserBean.setUserName(userName);
        umsUserBean.setSex(Convert.toByte(sex));
        umsUserBean.setFullName(strName);
        umsUserBean.setNickName(strName);
        umsUserBean.setRegIp(Tool.getClientIpLong(Tool.getRequest()));

        if (Objects.nonNull(userType))
        {
            umsUserBean.setUserType(Convert.toByte(userType));
        }

        umsUserMapper.insertUser(umsUserBean);

        //用户auth
        UmsUserAuth umsUserAuth = new UmsUserAuth();
        umsUserAuth.setUserName(userName);
        if (StrUtil.isNotBlank(phone))
        {
            umsUserAuth.setPhone(phone);
        }

        if (StrUtil.isNotBlank(pwd))
        {
            umsUserAuth.setPwd(passwordEncoder.encode(pwd));
        }
        else
        {
            umsUserAuth.setPwd("");
        }

        umsUserAuthMapper.insertUserAuth(umsUserAuth);

        //用户角色
        List<UmsUserRoles> umsUserRoles = new ArrayList<>();
        umsUserRoles.add(new UmsUserRoles(null, userName, UmsRole.ROLE_USER_ID, Define.SHOOL_SYSTEM_ID, null, null
                , null));
        umsUserRolesMapper.batchInsertUserRoles(umsUserRoles);

        //3.1 返回数据
        return this.getSystemUserInfoDetail(userName);
    }

    /**
     * 获取带角色的系统用户信息
     *
     * @param userName 用户名
     *
     * @return
     */
    protected Map<String, Object> getSystemUserInfoDetail(String userName)
    {
        Map<String, Object> result      = null;
        UmsUser             umsUserInfo = umsUserMapper.getInfoByUserName(userName);
        if (Objects.isNull(umsUserInfo))
        {
            throw new MyException(EC.ERROR_USER_NOT_EXIST);
        }

        //2.1 基本信息
        result = Tool.beanToMap(umsUserInfo);
        result.remove("description");

        //2.2 是否可用
        UmsUserAuth umsUserAuthInfo = umsUserAuthMapper.getInfoByUserName(userName);
        if (Objects.nonNull(umsUserAuthInfo))
        {
            result.putAll(Tool.beanToMap(umsUserAuthInfo));
        }

        //2.2 角色信息
        List<Map<String, Object>> roleList = new ArrayList<>();
        result.put("role_infos", roleList);

        List<String> userRoleIds = umsUserRolesMapper.getRoleIdsByUserName(userName,
                                                                           List.of(Define.SHOOL_SYSTEM_ID));
        if (null != userRoleIds)
        {
            UmsRole             umsRole  = null;
            Map<String, Object> roleInfo = null;
            for (String userRoleId : userRoleIds)
            {
                umsRole = umsRoleMapper.getInfoByRoleId(userRoleId);
                if (Objects.nonNull(umsRole))
                {
                    roleInfo = Tool.beanToMap(umsRole);
                    roleInfo.remove("description");
                    roleList.add(roleInfo);
                }
            }
        }

        return result;
    }

    /**
     * 启用禁用用户
     *
     * @param userName
     * @param enabled  状态
     *
     * @return
     */
    @Override
    public Integer enableUser(String userName, Integer enabled)
    {
        //1.1 用户名是否存在
        UmsUserAuth umsUserAuthInfo = umsUserAuthMapper.getInfoByUserName(userName);
        if (null == umsUserAuthInfo)
        {
            throw new MyException(EC.ERROR_USER_NOT_EXIST);
        }

        //2.1 修改状态
        umsUserAuthInfo.setEnabled(Convert.toByte(enabled));
        umsUserAuthMapper.updateByUserName(umsUserAuthInfo);

        return Define.YESI;
    }

    /**
     * 修改用户在机构或系统中的角色
     *
     * @param userName 用户名
     * @param roleIds  角色id号列表
     * @param schoolId 机构Id号
     * @param bDelete  是否删除角色
     * @param bInsert  是否新增角色
     *
     * @return
     */
    @Override
    public Integer modifyUserRoleIds(String userName, String roleIds, String schoolId, boolean bDelete, boolean bInsert)
    {
        List<String> desRoleIds = Arrays.asList(roleIds.split("\\|"));
        for (String roleId : desRoleIds)
        {
            UmsRole umsRoleInfo = umsRoleMapper.getInfoByRoleId(roleId);

            //是否有效
            if (null == umsRoleInfo)
            {
                throw new MyException(EC.ERROR_ROLE_NOT_EXIST);
            }

            //是否合法
            //系统角色只能赋值给系统
            if (umsRoleInfo.getIsSystem().equals(Define.YESI))
            {
                if (!schoolId.equals(Define.SHOOL_SYSTEM_ID))
                {
                    throw new MyException(EC.ERROR_PARAM_ILLEGAL);
                }
            }
            else
            {
                if (schoolId.equals(Define.SHOOL_SYSTEM_ID))
                {
                    throw new MyException(EC.ERROR_PARAM_ILLEGAL);
                }
            }
        }

        //2.0 获取权限列表
        List<String> sorRoleIds = umsUserRolesMapper.getRoleIdsByUserName(userName, List.of(schoolId));

        //2.1 移除的Id号列表
        if (bDelete)
        {
            if (null != sorRoleIds && sorRoleIds.size() > 0)
            {
                List<String> delRoleIds = Tool.delArrayBat(sorRoleIds, desRoleIds);
                if (delRoleIds.size() > 0)
                {
                    umsUserRolesMapper.batchDeleteByUserName(userName, schoolId, delRoleIds);
                }
            }
        }

        //2.2 新增的Id号列表
        if (bInsert)
        {
            List<String> insertRoleIds = Tool.delArrayBat(desRoleIds, sorRoleIds);
            if (insertRoleIds.size() > 0)
            {
                List<UmsUserRoles> umsUserRoles = new ArrayList<>();
                for (String insertRoleId : insertRoleIds)
                {
                    umsUserRoles.add(new UmsUserRoles(null, userName, insertRoleId, schoolId, null, null, null));
                }
                umsUserRolesMapper.batchInsertUserRoles(umsUserRoles);
            }
        }

        return Define.YESI;
    }
}
