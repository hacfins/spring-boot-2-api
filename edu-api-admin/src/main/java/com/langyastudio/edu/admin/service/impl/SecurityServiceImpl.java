package com.langyastudio.edu.admin.service.impl;

import com.langyastudio.edu.admin.bean.bo.AccountUserDetails;
import com.langyastudio.edu.admin.common.data.Define;
import com.langyastudio.edu.admin.service.SecurityService;
import com.langyastudio.edu.admin.service.base.BaseCacheService;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.util.Tool;
import com.langyastudio.edu.db.mapper.UmsApiMapper;
import com.langyastudio.edu.db.mapper.UmsUserAuthMapper;
import com.langyastudio.edu.db.mapper.UmsUserRolesMapper;
import com.langyastudio.edu.db.model.UmsApi;
import com.langyastudio.edu.db.model.UmsUser;
import com.langyastudio.edu.db.model.UmsUserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    BaseCacheService baseCacheService;

    @Autowired
    UmsUserAuthMapper umsUserAuthMapper;

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
        if (Boolean.FALSE.equals(this.isAdmin(userName)))
        {
            roleIds = umsUserRolesMapper.getRoleIdsByUserName(userName, schoolIds);
        }
        if (null == roleIds)
        {
            roleIds = new ArrayList<String>();
        }
        //任何人都属于访客角色
        //roleIds.add(UmsRole.ROLE_GUEST_ID);

        return baseCacheService.getApiListByRoleIds(roleIds);
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
        UmsUserAuth userInfo = umsUserAuthMapper.getInfoByUserName(userName);
        if (userInfo != null)
        {
            String schoolId = Tool.getParamEx("school_id", null);

            //获取接口调用的机构Id号
            List<UmsApi> resourceList = this.getAuthListAll(userName, schoolId);
            return new AccountUserDetails(userInfo, resourceList);
        }

        throw new MyException("用户名或密码错误");
    }
}
