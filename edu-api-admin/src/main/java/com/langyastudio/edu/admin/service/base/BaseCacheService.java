package com.langyastudio.edu.admin.service.base;

import com.langyastudio.edu.db.model.UmsApi;

import java.util.List;

/**
 * 用户缓存操作 Service
 *
 * @author jiangjiaxiong
 */
public interface BaseCacheService
{
    /**
     * 根据角色Id号获取权限列表
     *
     * @param roleIds 角色Id号列表
     */
    List<UmsApi> getApiListByRoleIds(List<String> roleIds);

    /**
     * 当角色改变时删除缓存
     * @param roleIds 角色Id号列表
     */
    void delApiListByRoleIds(List<String> roleIds);
}
