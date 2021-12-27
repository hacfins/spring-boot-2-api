package com.langyastudio.edu.common.middleware.aop;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.anno.LogField;
import com.langyastudio.edu.common.data.ResultInfo;
import com.langyastudio.edu.common.entity.UserAgentData;
import com.langyastudio.edu.common.entity.WebLog;
import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.service.LogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 异步写入日志实现类
 *
 * @author langyastudio
 */
@Component
@Log4j2
public class AyncLog
{
    @Autowired
    LogService logService;

    /**
     * 异步调用
     *
     * !! 不能使用HttpServletRequest等，因为它有生命周期
     *
     * @param method    方法
     * @param args      参数列表
     * @param startTime 开始操作时间
     * @param endTime   结束操作时间
     * @param result    执行结果
     * @param err       错误信息
     */
    @Async
    void writeLog(String schoolId, String userName,
                  String basePath, String requestMethod, String uri, String url, UserAgentData userAgent,
                  Method method, Object[] args, long startTime, long endTime,
                  Object result, Throwable err)
    {
        if (null != logService)
        {
            log.info("async task:");

            WebLog webLog = new WebLog();

            //逻辑信息
            webLog.setSchoolId(schoolId);
            webLog.setUserName(userName);

            //基本信息
            webLog.setBasePath(basePath);
            webLog.setUri(uri);
            webLog.setUrl(url);
            webLog.setMethod(requestMethod);
            webLog.setUserAgent(userAgent);
            webLog.setParameter(getParameter(method, args));
            webLog.setSpendTime((int) (endTime - startTime));
            webLog.setStartTime(startTime);

            LogField apiOperation = method.getAnnotation(LogField.class);
            webLog.setDescription(apiOperation.value());

            if (Objects.nonNull(result))
            {
                webLog.setResult(result);
            }
            //执行接口抛异常
            else
            {
                if (err instanceof MyException)
                {
                    MyException rExp = (MyException) err;
                    webLog.setResult(ResultInfo.data(rExp.getCode(), rExp.getMessage()));
                }
                else
                {
                    webLog.setResult(ResultInfo.data(err.getMessage()));
                }
            }

            logService.addLog(webLog);
        }
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private Map<String, Object> getParameter(Method method, Object[] args)
    {
        Map<String, Object> argList    = new HashMap<>();
        Parameter[]         parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++)
        {
            //将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null)
            {
                argList.putAll(BeanUtil.beanToMap(args[i]));
            }

            //将RequestParam注解修饰的参数作为请求参数
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null)
            {
                String key = parameters[i].getName();
                if (!StrUtil.isEmptyIfStr(requestParam.value()))
                {
                    key = requestParam.value();
                }
                argList.put(key, args[i]);
            }
        }

        if (argList.size() == 0)
        {
            return null;
        }
        return argList;
    }
}
