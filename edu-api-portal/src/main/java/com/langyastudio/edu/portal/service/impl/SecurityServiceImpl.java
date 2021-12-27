package com.langyastudio.edu.portal.service.impl;

import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.db.mapper.UmsApiMapper;
import com.langyastudio.edu.db.mapper.UmsRoleApisMapper;
import com.langyastudio.edu.db.mapper.UmsUserAuthMapper;
import com.langyastudio.edu.db.mapper.UmsUserRolesMapper;
import com.langyastudio.edu.db.model.UmsApi;
import com.langyastudio.edu.db.model.UmsRole;
import com.langyastudio.edu.db.model.UmsUser;
import com.langyastudio.edu.db.model.UmsUserAuth;
import com.langyastudio.edu.portal.bean.bo.AccountUserDetails;
import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.portal.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Spring security
 *
 * @author langyastudio
 */
@Service
public class SecurityServiceImpl implements SecurityService
{
    @Autowired
    UmsApiMapper umsApiMapper;

    @Autowired
    UmsUserRolesMapper umsUserRolesMapper;

    @Autowired
    UmsUserAuthMapper umsUserAuthMapper;

    @Autowired
    UmsRoleApisMapper umsRoleApisMapper;

    /**
     * 是否是admin
     *
     * @param userName 用户名
     *
     * @return bool
     */
    @Override
    public Boolean isAdmin(String userName)
    {
        return UmsUser.USER_ADMIN_NAME.equals(userName);
    }

    /*-------------------------------------------------------------------------------------------------------------- */
    // Spring security
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 获取API列表
     *
     * @return
     */
    @Override
    public List<UmsApi> getAuthListAll()
    {
        List<String> apiIdsAll = umsApiMapper.getApiIdsAll();

        //填充所有权限
        UmsApi       umsApiValue;
        List<UmsApi> umsApiList = new ArrayList<>();
        for (Iterator<String> iterator = apiIdsAll.iterator(); iterator.hasNext(); )
        {
            String apiId = iterator.next();
            umsApiValue = umsApiMapper.getInfoByApiId(apiId);
            if (null != umsApiValue)
            {
                umsApiList.add(umsApiValue);
            }
        }

        return umsApiList;
    }

    /**
     * 获取某个用户的权限列表
     *
     * @param userName 用户名
     * @param schoolId 机构Id号
     *
     * @return
     */
    @Override
    public List<UmsApi> getAuthListAll(String userName, String schoolId)
    {
        //1.1 表示系统设置
        List<String> schoolIds = new ArrayList<>();
        schoolIds.add(Define.SHOOL_SYSTEM_ID);

        //机构权限+系统权限
        if (null != schoolId && !Define.SHOOL_SYSTEM_ID.equals(schoolId))
        {
            schoolIds.add(schoolId);
        }

        //2.0 获取角色Id号
        List<String> roleIds = null;
        if (!this.isAdmin(userName))
        {
            roleIds = umsUserRolesMapper.getRoleIdsByUserName(userName, schoolIds);
            if (null == roleIds)
            {
                roleIds = new ArrayList<String>();
            }

            //任何人都属于访客角色
            roleIds.add(UmsRole.ROLE_GUEST_ID);
        }

        List<String> apiIds      = umsRoleApisMapper.getApiIdsByRoleId(roleIds);
        List<UmsApi> umsApiInfos = new ArrayList<>();
        if (null != apiIds)
        {
            UmsApi umsApi = null;
            for (String apiId : apiIds)
            {
                umsApi = umsApiMapper.getInfoByApiId(apiId);
                if (Objects.nonNull(umsApi))
                {
                    umsApiInfos.add(umsApi);
                }
            }
        }

        return umsApiInfos;
    }

    /**
     * 获取用户信息
     *
     * @param userName
     */
    @Override
    public UserDetails loadUserByUsername(String userName)
    {
        //获取用户信息
        UmsUserAuth userInfo = umsUserAuthMapper.getInfoByUserNameEx(userName);
        if (userInfo != null)
        {
            //String schoolId = Tool.getParamEx("school_id", null);

            //portal 不需要API的调用授权
            //List<UmsApi> resourceList = this.getAuthListAll(userName, schoolId);
            List<UmsApi> resourceList = new ArrayList<>();
            return new AccountUserDetails(userInfo, resourceList);
        }

        throw new MyException("用户名或密码错误");
    }
}
