package com.langyastudio.edu.security.aspect;

import com.langyastudio.edu.security.annotation.CacheException;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Redis缓存切面，防止Redis宕机影响正常业务逻辑
 * Created by macro on 2020/3/17.
 */
@Aspect
@Component
@Order(2)
@Log4j2
public class RedisCacheAspect
{
    @Pointcut("execution(public * com.langyastudio.admin.service.*CacheService.*(..)) || execution(public * com.langyastudio" +
            ".course.service.*CacheService.*(..))")
    public void cacheAspect()
    {
    }

    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable
    {
        Signature       signature       = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method          method          = methodSignature.getMethod();
        Object          result          = null;

        try
        {
            result = joinPoint.proceed();
        }
        catch (Throwable throwable)
        {
            //有CacheException注解的方法需要抛出异常
            if (method.isAnnotationPresent(CacheException.class))
            {
                throw throwable;
            }
            else
            {
                log.error(throwable.getMessage());
            }
        }

        return result;
    }

}
