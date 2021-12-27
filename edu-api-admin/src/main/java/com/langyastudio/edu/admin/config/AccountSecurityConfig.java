package com.langyastudio.edu.admin.config;

import com.langyastudio.edu.admin.service.SecurityService;
import com.langyastudio.edu.security.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Spring Security模块相关配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AccountSecurityConfig extends SecurityConfig
{
    @Autowired
    private SecurityService securityService;

    @Bean
    @Override
    public UserDetailsService userDetailsService()
    {
        //获取登录用户信息
        return username -> securityService.loadUserByUsername(username);
    }
}
