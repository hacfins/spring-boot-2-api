/*
package com.langyastudio.springboot.common.tool;

import org.apache.ibatis.cache.Cache;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MybatisCache implements Cache
{
    private static final String PREFIX = "SYS_CONFIG:";

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    private String                          id;
    private JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();

    private static RedisConnectionFactory redisConnectionFactory;

    public MybatisCache(final String id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value)
    {
        RedisClusterConnection conn = redisConnectionFactory
                .getClusterConnection();
        if (key == null)
            return;
        String strKey = PREFIX + key.toString();
        conn.set(strKey.getBytes(), jdkSerializer.serialize(value));
        conn.close();
    }

    @Override
    public Object getObject(Object key)
    {
        if (key != null)
        {
            String strKey = PREFIX + key.toString();
            RedisClusterConnection conn = redisConnectionFactory
                    .getClusterConnection();
            byte[] bs = conn.get(strKey.getBytes());
            conn.close();
            return jdkSerializer.deserialize(bs);
        }
        return null;
    }

    @Override
    public Object removeObject(Object key)
    {
        if (key != null)
        {
            RedisClusterConnection conn = redisConnectionFactory
                    .getClusterConnection();
            conn.del(key.toString().getBytes());
            conn.close();
        }
        return null;
    }

    @Override
    public void clear()
    {
        // 关键代码，data更新时清理缓存
        RedisClusterConnection conn = redisConnectionFactory
                .getClusterConnection();
        Set<byte[]> keys = conn.keys((PREFIX + "*").getBytes());
        for (byte[] bs : keys)
        {
            conn.del(bs);
        }
        conn.close();
    }

    @Override
    public int getSize()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ReadWriteLock getReadWriteLock()
    {
        return this.readWriteLock;
    }
}*/
