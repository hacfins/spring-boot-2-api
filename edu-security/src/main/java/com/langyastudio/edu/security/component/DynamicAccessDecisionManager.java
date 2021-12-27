package com.langyastudio.edu.security.component;

import cn.hutool.core.collection.CollUtil;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * 动态权限决策管理器 - 用于判断用户是否有访问权限
 */
public class DynamicAccessDecisionManager implements AccessDecisionManager
{
    @Override
    public void decide(Authentication authentication, Object object,
                       Collection<ConfigAttribute> configAttributes) throws AccessDeniedException,
            InsufficientAuthenticationException
    {
        // 当接口未被配置资源时直接放行
        if (CollUtil.isEmpty(configAttributes))
        {
            return;
        }

        // Todo: 是否有更好方案
        // 超级管理员
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails)
        {
            String userName = ((UserDetails) principal).getUsername();
            if(Objects.nonNull(userName) && "admin".equals(userName))
            {
                return;
            }
        }

        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext())
        {
            ConfigAttribute configAttribute = iterator.next();

            //将访问所需资源或用户拥有资源进行比对
            String needAuthority = configAttribute.getAttribute();
            for (GrantedAuthority grantedAuthority : authentication.getAuthorities())
            {
                if (needAuthority.trim().equals(grantedAuthority.getAuthority()))
                {
                    return;
                }
            }
        }

        throw new AccessDeniedException("抱歉，您没有访问权限");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute)
    {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass)
    {
        return true;
    }

}
