package com.langyastudio.edu.common.middleware.aop;

import com.langyastudio.edu.common.anno.LimitField;
import com.aliyun.oss.common.utils.StringUtils;
import com.langyastudio.edu.common.entity.LimitType;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.util.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 基于Redis的接口限流，如果未开启Redis，则该功能不生效
 */
@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class LimitAspect
{
    private final RedisTemplate<String, Object> redisTemplate;

    @Pointcut("@annotation(com.langyastudio.edu.common.anno.LimitField)")
    public void pointcut()
    {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable
    {
        Signature       signature       = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method          method          = methodSignature.getMethod();

        LimitField limitAnnotation = method.getAnnotation(LimitField.class);
        LimitType  limitType       = limitAnnotation.limitType();
        String     name            = limitAnnotation.name();
        String     key;
        int        limitPeriod     = limitAnnotation.period();
        int        limitCount      = limitAnnotation.count();

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ip = Tool.getClientIp(attributes.getRequest());

        switch (limitType)
        {
            case IP:
                key = ip;
                break;
            case CUSTOMER:
                key = limitAnnotation.key();
                break;
            default:
                key = StringUtils.upperCase(method.getName());
        }
        String keys = StringUtils.join(limitAnnotation.prefix() + "_", key, ip);

        String            luaScript   = buildLuaScript();
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        Long              count       = redisTemplate.execute(redisScript, List.of(keys), limitCount, limitPeriod);
        if (count != null && count.intValue() <= limitCount)
        {
            //log.info("IP:{} 第 {} 次访问key为 {}，描述为 [{}] 的接口", ip, count, keys, name);
            return joinPoint.proceed();
        }
        else
        {
            //log.error("key为 {}，描述为 [{}] 的接口访问超出频率限制", keys, name);
            throw new MyException("接口访问超出频率限制");
        }
    }

    /**
     * 限流脚本
     * 调用的时候不超过阈值，则直接返回并执行计算器自加。
     *
     * @return lua脚本
     */
    private String buildLuaScript()
    {
        return "local c" +
                "\nc = redis.call('get',KEYS[1])" +
                "\nif c and tonumber(c) > tonumber(ARGV[1]) then" +
                "\nreturn c;" +
                "\nend" +
                "\nc = redis.call('incr',KEYS[1])" +
                "\nif tonumber(c) == 1 then" +
                "\nredis.call('expire',KEYS[1],ARGV[2])" +
                "\nend" +
                "\nreturn c;";
    }
}
