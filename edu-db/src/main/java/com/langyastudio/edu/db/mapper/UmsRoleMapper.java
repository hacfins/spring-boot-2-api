package com.langyastudio.edu.db.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.langyastudio.edu.db.model.UmsRole;
import com.langyastudio.edu.common.data.PageIn;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * 角色表
 *
 * 逻辑Id号 - role_id
 */
@CacheConfig(cacheNames = "db" + ":umsrolemapper")
public interface UmsRoleMapper extends Mapper<UmsRole>
{
    /*-------------------------------------------------------------------------------------------------------------- */
    // cache
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     * 新增数据
     */
    Integer insertRole(UmsRole record);

    /**
     * 根据角色Id号获取角色信息
     */
    @Cacheable(key = "#roleId", unless = "#result == null")
    UmsRole getInfoByRoleId(@Param("roleId") String roleId);

    /**
     * 更新数据
     */
    @CacheEvict(key = "#record.roleId", beforeInvocation=false)
    Integer updateRoleByRoleId(UmsRole record);

    /**
     * 删除数据
     */
    @CacheEvict(key = "#roleId", beforeInvocation=false)
    Integer deleteByRoleId(@Param("roleId")String roleId);

    /*-------------------------------------------------------------------------------------------------------------- */
    // other
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     * 获取角色列表
     */
    PageIn<String> getRoleIdList(@Param("isSystem") Integer isSystem, @Param("isAsc") Integer isAsc, PageIn<?> range);

    /**
     * 根据名称获取角色信息
     */
    String existByRoleName(@Param("roleName")String roleName, @Param("roleId")String roleId);
}