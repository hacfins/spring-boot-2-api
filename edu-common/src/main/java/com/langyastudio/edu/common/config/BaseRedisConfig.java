package com.langyastudio.edu.common.config;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * redis配置类
 *
 * 【需要被继承】
 *
 * @author langyastudio
 */
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class BaseRedisConfig extends CachingConfigurerSupport
{
    public final static String _PREFIX = "edu_";

    /**
     * redis template相关配置
     * 使redis支持插入对象
     *
     * @param factory
     *
     * @return 方法缓存 Methods the cache
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory)
    {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        // 值采用json序列化
        template.setValueSerializer(this.fastJsonRedisSerializer(false));

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(this.fastJsonRedisSerializer(false));

        template.afterPropertiesSet();
        return template;
    }

    private FastJsonRedisSerializer<Object> fastJsonRedisSerializer(boolean WriteClassName)
    {
        //使用FastJsonRedisSerializer来序列化和反序列化redis的value值
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        FastJsonConfig                  fastJsonConfig          = fastJsonRedisSerializer.getFastJsonConfig();

        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty);
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);

        fastJsonConfig.setFeatures(Feature.SupportAutoType);

        if(WriteClassName)
        {
            fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteClassName);
        }

        //如果时间类型值为null，则返回空串
        //否则null类型的字段不返回!!!
/*        ValueFilter dateFilter = (Object var1, String var2, Object var3) -> {
            try {
                if (var3 == null && var1 != null &&
                        LocalDateTime.class.isAssignableFrom(var1.getClass().getDeclaredField(var2).getType())) {
                    return "";
                }
            } catch (Exception e) {
            }
            return var3;
        };
        fastJsonConfig.setSerializeFilters(dateFilter);*/

        return fastJsonRedisSerializer;
    }

    /*===============================注解缓存失效时间配置=============================*/
    /**
     * springboot2.x 设置redis缓存失效时间(注解)：
     *
     * @CacheConfig(cacheNames = "db")
     *
     * @Cacheable 表明对应方法的返回结果可以被缓存。首次调用后，下次就从缓存中读取结果，方法不会再被执行了
     * @CachePut 更新缓存，方法每次都会执行
     * @CacheEvict 清除缓存，方法每次都会执行
     * ...等等
     * <p>
     * 因为主要的业务逻辑在服务层实现，一般会把缓存注解加在服务层的方法上
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory)
    {
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),

                //3600秒 - // 默认策略，未配置的 key 会使用这个
                this.getRedisCacheConfigurationWithTtl(3600),
                this.getRedisCacheConfigurationMap()
        );
    }

    /**
     * 缓存的key是 包名+方法名+参数列表
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator()
    {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append("::" + method.getName() + ":");
            for (Object obj : objects)
            {
                sb.append(obj.toString());
            }

            return sb.toString();
        };
    }

    /**
     * 对每个缓存空间应用不同的配置
     *
     * @return
     */
    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap()
    {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();

        //进行过期时间配置
        //db缓存2小时
        redisCacheConfigurationMap.put("db", this.getRedisCacheConfigurationWithTtl(7200));

        return redisCacheConfigurationMap;
    }

    /**
     * 生成一个默认配置，通过config对象即可对缓存进行自定义配置
     *
     * @param seconds 设置缓存的默认过期时间
     *
     * @return
     */
    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds)
    {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        // 增加缓存前缀
        config = config.prefixCacheNameWith(_PREFIX)
                // 设置缓存的默认过期时间
                .entryTtl(Duration.ofSeconds(seconds))
                // 设置 key为string序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value为json序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer(true)))
        // 不缓存空值
        //.disableCachingNullValues()
        ;

        return config;
    }
}
