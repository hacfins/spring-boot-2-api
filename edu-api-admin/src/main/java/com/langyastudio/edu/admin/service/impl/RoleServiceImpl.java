package com.langyastudio.edu.admin.service.impl;

import com.langyastudio.edu.admin.common.data.Define;
import cn.hutool.core.util.IdUtil;
import com.langyastudio.edu.admin.service.base.BaseCacheService;
import com.langyastudio.edu.db.mapper.UmsRoleMapper;
import com.langyastudio.edu.db.mapper.UmsUserRolesMapper;
import com.langyastudio.edu.db.model.UmsRole;
import com.langyastudio.edu.admin.service.RoleService;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.data.PageIn;
import com.langyastudio.edu.common.data.PageInfo;
import com.langyastudio.edu.common.exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 角色管理业务逻辑层
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl implements RoleService
{
    @Autowired
    UmsRoleMapper umsRoleMapper;
    @Autowired
    UmsUserRolesMapper umsUserRolesMapper;

    @Autowired
    BaseCacheService baseCacheService;

    /**
     * 角色信息
     *
     * @param roleId 角色Id号
     *
     * @return
     */
    @Override
    public UmsRole getInfo(String roleId)
    {
        return umsRoleMapper.getInfoByRoleId(roleId);
    }

    /**
     * 角色列表
     *
     * @param isSystem 是否是系统角色
     * @param isAsc    是否升序
     * @param offSet   offset
     * @param pageSize size
     *
     * @return
     */
    @Override
    public PageInfo<UmsRole> getList(Integer isSystem, Integer isAsc, Integer offSet, Integer pageSize)
    {
        //获取原始数据
        PageIn<String> pageIns = new PageIn<>(offSet, pageSize);
        umsRoleMapper.getRoleIdList(isSystem, isAsc, pageIns);

        //循环获取记录列表
        List<UmsRole> umsRoleList = new ArrayList<>();
        long          total       = pageIns.getTotal();
        if (total > 0)
        {
            for (Iterator<String> it = pageIns.getRecords().iterator(); it.hasNext(); )
            {
                umsRoleList.add(umsRoleMapper.getInfoByRoleId(it.next()));
            }
        }

        PageInfo<UmsRole> pageInfoRole = new PageInfo<UmsRole>();
        pageInfoRole.setTotal(total);
        pageInfoRole.setList(umsRoleList);
        return pageInfoRole;
    }

    /**
     * 添加角色
     *
     * @param roleName    角色名称
     * @param viewSystem  是否访问系统
     * @param viewSchool  是否访问机构
     * @param isSystem    是否系统角色
     * @param description 描述信息
     *
     * @return
     *
     * @throws MyException
     */
    @Override
    public UmsRole add(String roleName, Integer viewSystem, Integer viewSchool, Integer isSystem, String description) throws MyException
    {
        //重名检查
        if (null != umsRoleMapper.existByRoleName(roleName, null))
        {
            throw new MyException(EC.ERROR_ROLE_NAME_RE);
        }

        String roleId = IdUtil.simpleUUID();

        UmsRole umsRoleBean = new UmsRole();
        umsRoleBean.setRoleId(roleId);
        umsRoleBean.setRoleName(roleName);
        umsRoleBean.setViewSystem(viewSystem);
        umsRoleBean.setViewSchool(viewSchool);
        umsRoleBean.setIsSystem(isSystem);
        umsRoleBean.setDescription(description);
        umsRoleBean.setRoleType(Define.NOI);

        int rtn = umsRoleMapper.insertRole(umsRoleBean);
        if (rtn != 0)
        {
            //修改sort
            umsRoleBean.setSort(umsRoleBean.getId());
            umsRoleMapper.updateRoleByRoleId(umsRoleBean);

            //返回原始数据
            return umsRoleMapper.getInfoByRoleId(roleId);
        }

        return null;
    }

    /**
     * 修改角色
     *
     * @param roleId      角色Id
     * @param roleName    角色名称
     * @param viewSystem  访问系统
     * @param viewSchool  访问学校
     * @param description 描述信息
     *
     * @return
     *
     * @throws MyException
     */
    @Override
    public Integer modify(String roleId, String roleName, Integer viewSystem, Integer viewSchool, String description) throws MyException
    {
        //是否存在检查
        if (null == umsRoleMapper.getInfoByRoleId(roleId))
        {
            throw new MyException(EC.ERROR_ROLE_NOT_EXIST);
        }

        //重名检查
        if (null != umsRoleMapper.existByRoleName(roleName,roleId))
        {
            throw new MyException(EC.ERROR_ROLE_NAME_RE);
        }

        UmsRole umsRoleBean = new UmsRole();
        umsRoleBean.setRoleId(roleId);
        umsRoleBean.setRoleName(roleName);
        umsRoleBean.setViewSystem(viewSystem);
        umsRoleBean.setViewSchool(viewSchool);
        umsRoleBean.setDescription(description);

        //更新缓存
        baseCacheService.delApiListByRoleIds(List.of(roleId));

        return umsRoleMapper.updateRoleByRoleId(umsRoleBean);
    }

    @Override
    @Transactional
    public Integer moveTo(String roleId, String desId) throws MyException
    {
        //获取原始数据
        UmsRole sorInfo = umsRoleMapper.getInfoByRoleId(roleId);
        UmsRole desInfo = umsRoleMapper.getInfoByRoleId(desId);
        if (null == sorInfo || null == desInfo)
        {
            throw new MyException(EC.ERROR_ROLE_NOT_EXIST);
        }

        //修改数据
        UmsRole mInfo = new UmsRole();
        mInfo.setRoleId(roleId);
        mInfo.setSort(desInfo.getSort());
        umsRoleMapper.updateRoleByRoleId(mInfo);

        mInfo.setRoleId(desId);
        mInfo.setSort(sorInfo.getSort());
        umsRoleMapper.updateRoleByRoleId(mInfo);

        return 1;
    }

    /**
     *
     * 角色是否存在
     *
     * @param roleName 角色名称
     * @param isSystem 是否是系统角色
     *
     * @return
     */
    @Override
    public Integer existName(String roleName, Integer isSystem)
    {
        if (null != umsRoleMapper.existByRoleName(roleName,null))
        {
            return Define.YESI;
        }

        return Define.NOI;
    }

    /**
     * 删除
     *
     * @param roleId 角色Id号
     *
     * @return
     */
    @Override
    public Integer del(String roleId)
    {
        UmsRole umsRole = umsRoleMapper.getInfoByRoleId(roleId);

        //1.0 是否存在检查
        if (null == umsRole)
        {
            throw new MyException(EC.ERROR_ROLE_NOT_EXIST);
        }
        //1.1 内置角色不能删除
        if(umsRole.getRoleType().equals(Define.YESI))
        {
            throw new MyException(EC.ERROR_ROLE_CANT_DEL);
        }

        //2.0 角色下有用户不可删除
        if(Objects.nonNull(umsUserRolesMapper.existUserNameByRoleId(roleId)))
        {
            throw new MyException(EC.ERROR_ROLE_USER_EXIST);
        }

        //更新缓存
        baseCacheService.delApiListByRoleIds(List.of(roleId));

        return umsRoleMapper.deleteByRoleId(roleId);
    }
}
