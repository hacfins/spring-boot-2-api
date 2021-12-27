package com.langyastudio.edu.common.middleware.aop;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.langyastudio.edu.common.anno.LogField;
import com.langyastudio.edu.common.util.Tool;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
@Order(1)
public class WebLogAspect
{
    @Autowired
    AyncLog ayncLog;

    /**
     * 包及其子包下所有类中的所有方法都应用切面里的通知
     */
    @Pointcut("execution(public * com.langyastudio.edu.*.controller..*.*(..))")
    public void webLog()
    {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable
    {
    }

    @AfterReturning(value = "webLog()", returning = "ret")
    public void doAfterReturning(Object ret) throws Throwable
    {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable
    {
        //只记录有LogField标记的请求
        Signature       signature       = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method          method          = methodSignature.getMethod();

        Object result = null;
        if (method.isAnnotationPresent(LogField.class))
        {
            long      startTime = System.currentTimeMillis();
            Throwable err       = null;
            try
            {
                result = joinPoint.proceed();
            }
            catch (Throwable e)
            {
                err = e;
                throw e;
            }
            finally
            {
                ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();

                String schoolId      = Tool.getParamEx("school_id", request);
                String userName      = (String)request.getAttribute("user_name");
                String urlStr        = request.getRequestURL().toString();
                String basePath      = StrUtil.removeSuffix(urlStr, URLUtil.url(urlStr).getPath());
                String requestMethod = request.getMethod();
                String uri           = request.getRequestURI();
                String url           = request.getRequestURL().toString();

                //写入日志
                ayncLog.writeLog(schoolId, userName,
                                 basePath, requestMethod, uri, url, Tool.getUserAgentInfo(request),
                                 method, joinPoint.getArgs(), startTime, System.currentTimeMillis(),
                                 result, err);
            }
        }
        else
        {
            result = joinPoint.proceed();
        }

        return result;
    }
}
