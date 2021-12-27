package com.langyastudio.edu.admin.service;

import com.langyastudio.edu.db.model.UmsRole;
import com.langyastudio.edu.common.data.PageInfo;
import com.langyastudio.edu.common.exception.MyException;

/**
 * 角色管理业务逻辑层
 */
public interface RoleService extends BaseService
{
    /**
     * 获取角色信息
     *
     * @param roleId 角色Id号
     *
     * @return UmsRole
     */
    UmsRole getInfo(String roleId);

    /**
     * 获取角色列表
     *
     * @param isSystem 是否是系统角色
     * @param isAsc    是否升序
     *
     * @return
     */
    PageInfo<UmsRole> getList(Integer isSystem, Integer isAsc, Integer offSet, Integer pageSize);

    /**
     * 添加角色
     *
     * @param roleName    角色名称
     * @param viewSystem  是否访问系统
     * @param viewSchool  是否访问机构
     * @param isSystem    是否系统角色
     * @param description 描述信息
     *
     * @return UmsRole
     */
    UmsRole add(String roleName, Integer viewSystem, Integer viewSchool, Integer isSystem, String description) throws MyException;

    /**
     * 修改角色
     *
     * @param roleName    角色名称
     * @param viewSystem  是否访问系统
     * @param viewSchool  是否访问机构
     * @param description 描述信息
     *
     * @return
     */
    Integer modify(String roleId, String roleName, Integer viewSystem, Integer viewSchool, String description) throws MyException;

    /**
     * 移动角色
     *
     * @param roleId 原始role Id号
     * @param desId  目的role Id号
     *
     * @return
     */
    Integer moveTo(String roleId, String desId) throws MyException;

    /**
     * 角色是否存在
     *
     * @param roleName 角色名称
     * @param isSystem 是否是系统角色
     *
     * @return
     */
    Integer existName(String roleName, Integer isSystem);

    /**
     * 删除角色
     *
     * @param roleId 角色Id号
     *
     * @return
     */
    Integer del(String roleId);

}
