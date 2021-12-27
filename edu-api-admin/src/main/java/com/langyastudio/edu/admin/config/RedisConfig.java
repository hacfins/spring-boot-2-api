package com.langyastudio.edu.admin.config;

import com.langyastudio.edu.common.config.BaseRedisConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * redis配置类
 */
@Configuration
@EnableCaching
public class RedisConfig extends BaseRedisConfig
{
}
