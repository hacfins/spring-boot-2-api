package com.langyastudio.edu.admin.service;

import java.util.Map;

/**
 * 基类
 */
public interface BaseService
{
    /*-------------------------------------------------------------------------------------------------------------- */
    // 机构
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     * 获取登录用户名
     *
     * @param bNeedLogin 未登录是否抛出异常
     *
     * @return 用户名
     */
    String getUserName(boolean bNeedLogin);

    /**
     * 添加系统用户
     *
     * @param userName 用户名
     * @param fullName 姓名
     * @param phone    手机号
     *
     * @return
     */
    Map<String, Object> addSystemUser(String userName, String pwd, String fullName, String phone, Integer sex,
                                      Byte userType, Integer checkUserName);

    /**
     * 启用禁用用户
     *
     * @param enabled 状态
     *
     * @return
     */
    Integer enableUser(String userName, Integer enabled);

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
    Integer modifyUserRoleIds(String userName, String roleIds, String schoolId, boolean bDelete, boolean bInsert);
}
