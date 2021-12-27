package com.langyastudio.edu.portal.service;

import com.langyastudio.edu.db.model.UmsApi;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Spring security
 *
 * @author jiangjiaxiong
 */
public interface SecurityService
{
    Boolean isAdmin(String userName);

    /*-------------------------------------------------------------------------------------------------------------- */
    // Spring security
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 根据角色Id号获取API权限列表
     *
     * @return
     */
    List<UmsApi> getAuthListAll();

    /**
     * 获取某个用户的权限列表
     *
     * @param userName 用户名
     *
     * @return
     */
    List<UmsApi> getAuthListAll(String userName, String schoolId);

    /**
     * 获取用户信息
     *
     * @param userName 用户名
     */
    UserDetails loadUserByUsername(String userName);
}
