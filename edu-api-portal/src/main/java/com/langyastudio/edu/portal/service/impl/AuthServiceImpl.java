package com.langyastudio.edu.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.util.Tool;
import com.langyastudio.edu.db.mapper.UmsApiMapper;
import com.langyastudio.edu.db.mapper.UmsRoleApisMapper;
import com.langyastudio.edu.db.mapper.UmsRoleMapper;
import com.langyastudio.edu.db.mapper.UmsUserRolesMapper;
import com.langyastudio.edu.db.model.UmsApi;
import com.langyastudio.edu.db.model.UmsRole;
import com.langyastudio.edu.portal.common.data.Define;
import com.langyastudio.edu.portal.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthServiceImpl extends BaseServiceImpl implements AuthService
{
    @Autowired
    UmsUserRolesMapper umsUserRolesMapper;

    @Autowired
    UmsRoleApisMapper umsRoleApisMapper;

    @Autowired
    UmsApiMapper umsApiMapper;

    @Autowired
    UmsRoleMapper umsRoleMapper;

    /**
     * 用户菜单列表
     *
     * @param schoolId 机构Id号，不传时表示获取系统菜单视图
     *
     * @return
     */
    @Override
    public Map<String, Object> getViewList(String schoolId)
    {
        //1.0 获取登录用户名
        String userName = this.getUserName(false);

        //1.1 表示系统设置
        if (StrUtil.isBlankIfStr(schoolId))
        {
            schoolId = Define.SHOOL_SYSTEM_ID;
        }
        else
        //机构是否有效
        {

        }

        //2.0 获取角色Id号 key->value
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("view_system", Define.NO);
        resultMap.put("view_school", Define.NO);
        resultMap.put("view_list", new ArrayList[]{});

        List<Map<String, Object>> roleList = new ArrayList<>();
        resultMap.put("role_infos", roleList);

        List<String> sorRoleIds = umsUserRolesMapper.getRoleIdsByUserName(userName, List.of(schoolId));
        if (null != sorRoleIds)
        {
            Map<String, Object> roleInfo = null;
            for (String userRoleId : sorRoleIds)
            {
                roleInfo = Tool.beanToMap(umsRoleMapper.getInfoByRoleId(userRoleId));
                roleInfo.remove("description");
                roleList.add(roleInfo);
            }
        }

        //2.1 获取权限视图
        List<String> roleIds = null;
        if (Objects.nonNull(userName) && !this.isAdmin(userName))
        {
            roleIds = umsUserRolesMapper.getRoleIdsByUserName(userName, List.of(schoolId));
        }

        //管理员拥有所有权限
        if (this.isAdmin(userName))
        {
            //为了获取所有值
            roleIds = null;
        }
        else
        {
            if (Objects.isNull(roleIds))
            {
                roleIds = new ArrayList<String>();
            }

            //任何人都属于访客角色
            roleIds.add(UmsRole.ROLE_GUEST_ID);
        }

        //访问子菜单
        List<String> apiIds = umsRoleApisMapper.getMenuApiIds(roleIds);
        if (null != apiIds)
        {
            UmsApi       umsApi      = null;
            List<UmsApi> umsApiInfos = new ArrayList<>();
            for (String apiId : apiIds)
            {
                umsApi = umsApiMapper.getInfoByApiId(apiId);

                //访问后台标志
                if ("menu_system".equals(umsApi.getApiModule()))
                {
                    resultMap.put("view_system", Define.YES);
                }
                else if ("menu_school".equals(umsApi.getApiModule()))
                {
                    resultMap.put("view_school", Define.YES);
                }

                umsApiInfos.add(umsApi);
            }

            resultMap.put("view_list", umsApiInfos);
        }

        return resultMap;
    }
}
