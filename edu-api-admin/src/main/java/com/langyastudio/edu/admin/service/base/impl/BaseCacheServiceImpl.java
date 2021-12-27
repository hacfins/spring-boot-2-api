package com.langyastudio.edu.admin.service.base.impl;

import com.langyastudio.edu.admin.bean.vo.UmsApiListVO;
import com.langyastudio.edu.admin.service.base.BaseCacheService;
import com.langyastudio.edu.common.util.RedisT;
import com.langyastudio.edu.db.mapper.UmsApiMapper;
import com.langyastudio.edu.db.mapper.UmsRoleApisMapper;
import com.langyastudio.edu.db.model.UmsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author langyastudio
 */
@Service
public class BaseCacheServiceImpl implements BaseCacheService
{
    @Autowired
    RedisT redisService;

    @Value("${spring.redis.expire.common:86400}")
    private Long REDIS_EXPIRE;

    @Value("${spring.redis.key.api-list:ums-admin-apilist-}")
    private String REDIS_KEY_API_LIST;

    @Autowired
    UmsRoleApisMapper umsRoleApisMapper;

    @Autowired
    UmsApiMapper umsApiMapper;

    /**
     * 根据角色Id号获取权限列表
     *
     * @param roleIds 角色Id号列表
     */
    @Override
    public List<UmsApi> getApiListByRoleIds(List<String> roleIds)
    {
        List<UmsApi> resultApiInfos = new ArrayList<>();

        if (Objects.nonNull(roleIds) && roleIds.size() > 0)
        {
            for (String roleId : roleIds)
            {
                //尝试从缓存中取值
                UmsApiListVO apiListVO = redisService.get(REDIS_KEY_API_LIST + roleId, UmsApiListVO.class);
                if (Objects.isNull(apiListVO))
                {
                    List<String> apiIds = umsRoleApisMapper.getApiIdsByRoleId(List.of(roleId));
                    if (null != apiIds)
                    {
                        UmsApi umsApi = null;
                        List<UmsApi> apiArrayList = new ArrayList<>();
                        for (String apiId : apiIds)
                        {
                            umsApi = umsApiMapper.getInfoByApiId(apiId);
                            if (Objects.nonNull(umsApi))
                            {
                                apiArrayList.add(umsApi);
                            }
                        }
                        resultApiInfos.addAll(apiArrayList);

                        //设置缓存值
                        apiListVO = new UmsApiListVO(apiArrayList);
                        redisService.set(REDIS_KEY_API_LIST + roleId, apiListVO, REDIS_EXPIRE);
                    }
                }
                else
                {
                    resultApiInfos.addAll(apiListVO.getRoleApiInfos());
                }
            }
        }

        //移除重复数据
        TreeSet<UmsApi> treeSet = new TreeSet<>(Comparator.comparing(UmsApi::getApiId));
        treeSet.addAll(resultApiInfos);

        return new ArrayList<>(treeSet);
    }

    /**
     * 当角色改变时删除缓存
     *
     * @param roleIds 角色Id号列表
     */
    @Override
    public void delApiListByRoleIds(List<String> roleIds)
    {
        if (Objects.nonNull(roleIds) && roleIds.size() > 0)
        {
            List<String> keys = roleIds.stream()
                    .map(roleId -> REDIS_KEY_API_LIST + roleId)
                    .collect(Collectors.toList());

            redisService.del(keys.toArray(String[]::new));
        }
    }
}
