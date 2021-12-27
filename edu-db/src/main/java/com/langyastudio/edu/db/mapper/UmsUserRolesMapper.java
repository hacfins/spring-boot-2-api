package com.langyastudio.edu.db.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.langyastudio.edu.db.model.UmsUserRoles;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 用户角色映射表
 * 逻辑Id号 - user_name+school_id
 *
 * 系统后台也理解为一个机构Id号
 */
public interface UmsUserRolesMapper extends Mapper<UmsUserRoles>
{
    /**
     * 批量插入username school - roleid映射数据
     *
     * @param list UserRoles列表
     *
     * @return
     */
    Integer batchInsertUserRoles(@Param("list") List<UmsUserRoles> list);

    /**
     * 批量删除 username school - roleid映射数据
     *
     * @param userName 用户名
     * @param schoolId 机构Id号
     * @param roleIds  角色Id号列表
     *
     * @return
     */
    Integer batchDeleteByUserName(@Param("userName") String userName,
                                  @Param("schoolId") String schoolId,
                                  @Param("roleIds") List<String> roleIds);

    /**
     * 获取用户对应的RoleId号列表
     *
     * @param userName  用户名
     * @param schoolIds 机构Id号列表
     *
     * @return
     */
    List<String> getRoleIdsByUserName(@Param("userName") String userName,
                                      @Param("schoolIds") List<String> schoolIds);

    /**
     * 角色下时候有用户
     *
     * @param roleId 角色Id号
     *
     * @return
     */
    String existUserNameByRoleId(@Param("roleId") String roleId);
}