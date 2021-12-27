package com.langyastudio.edu.portal.config;

import com.langyastudio.edu.db.model.UmsApi;
import com.langyastudio.edu.portal.service.SecurityService;
import com.langyastudio.edu.security.component.DynamicSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring Dynamic Security
 *
 * @author langyastudio
 */
@Service("dynamicSecurityService")
public class DynamicSecurityImpl implements DynamicSecurityService
{
    @Autowired
    private SecurityService securityService;

    /**
     * 加载资源ANT通配符和资源对应MAP
     */
    @Override
    public Map<String, ConfigAttribute> loadDataSource()
    {
        Map<String, ConfigAttribute> map          = new ConcurrentHashMap<>();
        List<UmsApi>                 resourceList = securityService.getAuthListAll();
        for (UmsApi resource : resourceList)
        {
            map.put(resource.getApiUrl(),
                    new org.springframework.security.access.SecurityConfig(resource.getApiId() + ":" + resource.getApiName()));
        }

        return map;
    }
}
