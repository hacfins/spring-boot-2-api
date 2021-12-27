package com.langyastudio.edu.db.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.langyastudio.edu.db.model.UmsRoleApis;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * UmsRole Api 映射表
 * 逻辑Id号 - roleId + apiId
 */
public interface UmsRoleApisMapper extends Mapper<UmsRoleApis>
{
    /**
     * 批量插入roleId apiId映射数据
     *
     * @param list RoleApi 列表
     *
     * @return
     */
    Integer batchInsertRoleApis(@Param("list") List<UmsRoleApis> list);

    /**
     * 批量删除
     *
     * @param roleId 角色Id号
     * @param apiIds apiId列表
     *
     * @return
     */
    Integer batchDeleteByRoleId(@Param("roleId") String roleId, @Param("apiIds") List<String> apiIds);

    /**
     * 获取ApiIds
     *
     * @param roleIds 角色Id号
     *
     * @return
     */
    List<String> getApiIdsByRoleId(@Param("roleIds") List<String> roleIds);

    /**
     * 获取所有菜单的api_id
     *
     * @return
     */
    List<String> getMenuApiIds(@Param("roleIds") List<String> roleIds);
}