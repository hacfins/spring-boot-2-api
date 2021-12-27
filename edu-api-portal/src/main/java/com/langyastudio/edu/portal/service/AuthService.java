package com.langyastudio.edu.portal.service;

import java.util.Map;

/**
 * 权限管理
 */
public interface AuthService extends BaseService
{
    /**
     * 用户菜单视图
     *
     * @param schoolId 机构Id号，不传时表示获取系统菜单视图
     *
     * @return
     */
    Map<String, Object> getViewList(String schoolId);
}
