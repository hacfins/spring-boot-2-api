package com.langyastudio.edu.admin.service;

import com.langyastudio.edu.db.model.UmsApi;
import com.langyastudio.edu.common.data.PageInfo;

import java.util.Map;

/**
 * 权限管理
 */
public interface AuthService extends BaseService
{
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
    PageInfo<UmsApi> getAuthListByRoleId(String roleId);

    /**
     * 设置角色映射的API权限列表
     *
     * @param roleId 角色Id号
     * @param apiIds API权限Id号列表
     *
     * @return
     */
    Integer setAuthByRoleId(String roleId, String apiIds);

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
    PageInfo<Map<String, Object>> getUserList(String roleId, String userNameKey, String fullNameKey, String phoneKey,
                                              Integer isAsc, Integer offset, Integer pageSize);

    /**
     * 设置用户映射的角色
     *
     * @param userName 用户名
     * @param roleIds  角色Id号列表
     * @param schoolId 机构Id号
     *
     * @return
     */
    Integer setUserRoles(String userName, String roleIds, String schoolId);

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
    Integer modifyUser(String userName, String fullName, String phone, Integer sex);

    /**
     * 重置密码
     *
     * @param userName 用户名
     *
     * @return
     */
    Integer resetPwd(String userName);
}
