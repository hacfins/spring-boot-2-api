package com.langyastudio.edu.portal.service;

import java.util.Map;

/**
 * 基础服务
 */
public interface BaseService
{
    /**
     * 获取登录用户名
     *
     * @param bNeedLogin 未登录是否抛出异常
     *
     * @return 用户名
     */
    String getUserName(boolean bNeedLogin);

    /**
     * 是否是admin
     *
     * @param userName 用户名
     * @return bool
     */
    Boolean isAdmin(String userName);

    /**
     * 登录功能
     *
     * @param username 用户名
     * @param password 密码
     *
     * @return 生成的JWT的token
     */
    String login(String username, String password);

    /**
     * 添加系统用户
     *
     * @param userName 用户名
     * @param fullName 姓名
     * @param phone    手机号
     *
     * @return
     */
    Map<String, Object> addSystemUser(String userName,  String pwd, String fullName, String phone, Integer sex);
}
