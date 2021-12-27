package com.langyastudio.edu.admin.bean.bo;

import com.langyastudio.edu.common.util.RedisT;
import me.zhyd.oauth.cache.AuthCacheConfig;
import me.zhyd.oauth.cache.AuthStateCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 扩展Redis版的state缓存
 */
@Component
public class AuthStateRedisCache implements AuthStateCache
{
    @Autowired
    RedisT redisT;

    /**
     * 存入缓存，默认3分钟
     *
     * @param key   缓存key
     * @param value 缓存内容
     */
    @Override
    public void cache(String key, String value)
    {
        redisT.set(key, value, AuthCacheConfig.timeout);
    }

    /**
     * 存入缓存
     *
     * @param key     缓存key
     * @param value   缓存内容
     * @param timeout 指定缓存过期时间（毫秒）
     */
    @Override
    public void cache(String key, String value, long timeout)
    {
        redisT.set(key, value, timeout);
    }

    /**
     * 获取缓存内容
     *
     * @param key 缓存key
     *
     * @return 缓存内容
     */
    @Override
    public String get(String key)
    {
        return redisT.get(key);
    }

    /**
     * 是否存在key，如果对应key的value值已过期，也返回false
     *
     * @param key 缓存key
     *
     * @return true：存在key，并且value没过期；false：key不存在或者已过期
     */
    @Override
    public boolean containsKey(String key)
    {
        return redisT.hasKey(key);
    }
}
