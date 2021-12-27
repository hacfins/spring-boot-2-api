package com.langyastudio.springboot.mapper;

import com.langyastudio.springboot.model.UmsUser;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

@CacheConfig(cacheNames = "db" + ":UmsUserMapper")
public interface UmsUserMapper
{
    int insertSelective(UmsUser record);

    /**
     * 软删除数据
     *
     * @param userName 用户名
     * @return
     */
    //1.0
    // @CacheEvict(key = "#userName", beforeInvocation=true)
    //or
    //2.0
    //多个 CacheEvict CachePut or Cacheable
    @Caching(evict={
            @CacheEvict(key = "#userName", beforeInvocation=true),
            @CacheEvict(key = "#userName + '_ex'", beforeInvocation=true)
    })
    int deleteByPrimaryKey(String userName);

    /**
     * 更新数据
     *
     * @param record UmsUser
     * @return
     */
    @CacheEvict(key = "#record.userName", beforeInvocation=true)
    int updateByPrimaryKeySelective(UmsUser record);

    /**
     * 信息
     *
     * @param userName 用户名
     * @return
     */
    //只有非null数据才会缓存
    @Cacheable(key = "#userName", unless = "#result == null")
    UmsUser selectByPrimaryKey(String userName);

    /**
     * 信息 - 含有软删除数据
     *
     * @param userName 用户名
     * @return
     */
    //只有非null数据才会缓存
    @Cacheable(key = "#userName + '_ex'", unless = "#result == null")
    UmsUser selectByPrimaryKeyEx(String userName);
}