package com.langyastudio.edu.common.util;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.cache.Cache;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * MyBatis二级缓存Redis实现
 *
 * 重点处理以下几个问题
 * 1、缓存穿透：存储空值解决，MyBatis框架实现
 * 2、缓存击穿：使用互斥锁，我们自己实现
 * 3、缓存雪崩：缓存有效期设置为一个随机范围，我们自己实现
 * 4、读写性能：redis key不能过长，会影响性能，这里使用SHA-256计算摘要当成key
 */
@Log4j2
public class MybatisCache implements Cache
{
    /**
     * 统一字符集
     */
    private static final    String        CHARSET       = "utf-8";
    /**
     * key摘要算法
     */
    private static final    String        ALGORITHM     = "SHA-256";
    /**
     * 统一缓存头
     */
    private static final    String        CACHE_NAME    = "MyBatis:";
    /**
     * 读写锁：解决缓存击穿
     */
    private final           ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /**
     * 表空间ID：方便后面的缓存清理
     */
    private final           String        id;
    /**
     * redis服务接口：提供基本的读写和清理
     */
    private static volatile RedisT        redisService;

    /////////////////////// 解决缓存雪崩，具体范围根据业务需要设置合理值 //////////////////////////
    /**
     * 缓存最小有效期
     */
    private static final int MIN_EXPIRE_MINUTES = 3600;
    /**
     * 缓存最大有效期
     */
    private static final int MAX_EXPIRE_MINUTES = 7200;

    /**
     * MyBatis给每个表空间初始化的时候要用到
     *
     * @param id 其实就是namespace的值
     */
    public MybatisCache(String id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    /**
     * 获取ID
     *
     * @return 真实值
     */
    @Override
    public String getId()
    {
        return id;
    }

    /**
     * 创建缓存
     *
     * @param key   其实就是sql语句
     * @param value sql语句查询结果
     */
    @Override
    public void putObject(Object key, Object value)
    {
        try
        {
            String strKey = getKey(key);
            // 有效期为1~2小时之间随机，防止雪崩
            int expireMinutes = RandomUtil.randomInt(MIN_EXPIRE_MINUTES, MAX_EXPIRE_MINUTES);
            getRedisService().set(strKey, value, expireMinutes);
            log.debug("Put cache to redis, id={}", id);
        }
        catch (Exception e)
        {
            log.error("Redis put failed, id=" + id, e);
        }
    }

    /**
     * 读取缓存
     *
     * @param key 其实就是sql语句
     *
     * @return 缓存结果
     */
    @Override
    public Object getObject(Object key)
    {
        try
        {
            String strKey = getKey(key);
            log.debug("Get cache from redis, id={}", id);
            return getRedisService().get(strKey);
        }
        catch (Exception e)
        {
            log.error("Redis get failed, fail over to db", e);
            return null;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 其实就是sql语句
     *
     * @return 结果
     */
    @Override
    public Object removeObject(Object key)
    {
        try
        {
            String strKey = getKey(key);
            getRedisService().del(strKey);
            log.debug("Remove cache from redis, id={}", id);
        }
        catch (Exception e)
        {
            log.error("Redis remove failed", e);
        }
        return null;
    }

    /**
     * 缓存清理
     * 网上好多博客这里用了flushDb甚至是flushAll，感觉好坑鸭！
     * 应该是根据表空间进行清理
     */
    @Override
    public void clear()
    {
        try
        {
            log.debug("clear cache, id={}", id);
            String hsKey = CACHE_NAME + id;

            // 获取CacheNamespace所有缓存key
            Map<Object, Object> idMap = getRedisService().hashEntries(hsKey);
            if (!idMap.isEmpty())
            {
                Set<Object> keySet = idMap.keySet();
                Set<String> keys   = new HashSet<>(keySet.size());

                keySet.forEach(item -> keys.add(item.toString()));

                // 清空CacheNamespace所有缓存
                getRedisService().del(keys.toArray(new String[0]));
                // 清空CacheNamespace
                getRedisService().del(hsKey);
            }
        }
        catch (Exception e)
        {
            log.error("clear cache failed", e);
        }
    }

    /**
     * 获取缓存大小，暂时没用上
     *
     * @return 长度
     */
    @Override
    public int getSize()
    {
        return 0;
    }

    /**
     * 获取读写锁：为了解决缓存击穿
     *
     * @return 锁
     */
    @Override
    public ReadWriteLock getReadWriteLock()
    {
        return readWriteLock;
    }

    /**
     * 计算出key的摘要
     *
     * @param cacheKey CacheKey
     *
     * @return 字符串key
     */
    private String getKey(Object cacheKey)
    {
        String cacheKeyStr = cacheKey.toString();
        String key = CACHE_NAME + cacheKeyStr;

        // 在redis额外维护CacheNamespace创建的key
        // clear的时候只清理当前CacheNamespace的数据
        getRedisService().hashSet(CACHE_NAME + id, key, "1");
        return key;
    }

    /**
     * 获取Redis服务接口
     * 使用双重检查保证线程安全
     *
     * @return 服务实例
     */
    private RedisT getRedisService()
    {
        if (redisService == null)
        {
            synchronized (RedisT.class)
            {
                if (redisService == null)
                {
                    redisService = SpringContextT.getBean(RedisT.class);
                }
            }
        }
        return redisService;
    }
}

