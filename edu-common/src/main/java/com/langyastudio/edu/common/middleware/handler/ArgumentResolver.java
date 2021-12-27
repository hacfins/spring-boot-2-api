package com.langyastudio.edu.common.middleware.handler;

import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.exception.MyException;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

/**
 * 请求参数处理
 *
 * 请求参数下划线改为驼峰
 * 时间转为LocalDate、LocalDateTime
 */
@Log4j2
public class ArgumentResolver implements HandlerMethodArgumentResolver
{
    @Override
    public boolean supportsParameter(@NotNull MethodParameter methodParameter)
    {
        // return methodParameter.hasParameterAnnotation(ParamModel.class);
        // 全局使用的话，直接返回true
        return true;
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NotNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws MyException
    {
        Class<?> paramType = parameter.getParameterType();

        //【1】基本类型
        if (isSimpleType(paramType))
        {
            String paramName  = parameter.getParameterName();
            if(null == paramName)
            {
                return null;
            }

            //1.1 获取参数值
            String paramValue = webRequest.getParameter(StrUtil.toUnderlineCase(paramName));
            if (paramValue == null)
            {
                paramValue = webRequest.getParameter(paramName);
            }

            //1.2 获取注解默认值
            if (null == paramValue)
            {
                RequestParam paramAnno = parameter.getParameterAnnotation(RequestParam.class);
                if(paramAnno != null)
                {
                    paramValue = paramAnno.defaultValue();
                }
            }
            if(null == paramValue)
            {
                return null;
            }

            //1.3 转换Object
            try
            {
                //日期字段
                if(isLocalDateTimeType(paramType))
                {
                    return LocalDateTime.parse(paramValue, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                else if(isLocalDateType(paramType))
                {
                    return LocalDate.parse(paramValue, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                //简单字段
                else
                {
                    return paramType.getConstructor(String.class).newInstance(paramValue);
                }
            }
            catch (Exception e)
            {
                throw new MyException(EC.ERROR_PARAM_EXCEPTION.getCode(), "ArgumentResolver 参数转换异常");
            }
        }
        //【2】Object 类型
        else
        {
            Object parameterTypeObj;
            try
            {
                parameterTypeObj = paramType.getDeclaredConstructor().newInstance();
            }
            catch (Exception e)
            {
                throw new MyException(EC.ERROR_PARAM_EXCEPTION.getCode(), "ArgumentResolver 参数转换异常");
            }

            BeanWrapper      wrapper    = PropertyAccessorFactory.forBeanPropertyAccess(parameterTypeObj);
            Iterator<String> paramNames = webRequest.getParameterNames();
            while (paramNames.hasNext())
            {
                String paramName   = paramNames.next();
                Object oParamValue = webRequest.getParameter(paramName);
                try
                {
                    wrapper.setPropertyValue(StrUtil.toCamelCase(paramName), oParamValue);
                }
                catch (BeansException e)
                {
                    log.info("ArgumentResolver 下划线转驼峰时出错，实体类 {} 中无对应属性：{}",
                             (oParamValue !=null) ? oParamValue.getClass().getName() : paramName , paramName);
                }
            }

            // 参数校验

            return parameterTypeObj;
        }
    }

    /**
     * 判断是否为日期时间类型
     *
     * @param clazz
     *
     * @return
     */
    private static boolean isLocalDateTimeType(Class<?> clazz)
    {
        String  simpleName = clazz.getSimpleName();
        if (0 == "localdatetime".compareTo(simpleName.toLowerCase()))
        {
            return true;
        }

        return false;
    }

    /**
     * 判断是否为日期类型
     *
     * @param clazz
     *
     * @return
     */
    private static boolean isLocalDateType(Class<?> clazz)
    {
        String  simpleName = clazz.getSimpleName();
        if (0 == "localdate".compareTo(simpleName.toLowerCase()))
        {
            return true;
        }

        return false;
    }

    /**
     * 判断是否为基本数据类型、简单类型
     *
     * @param clazz
     *
     * @return
     */
    private static boolean isSimpleType(Class<?> clazz)
    {
        String  simpleName = clazz.getSimpleName();
        boolean simple     = false;
        switch (simpleName.toLowerCase())
        {
            case "byte":
            case "short":
            case "int":
            case "integer":
            case "long":
            case "float":
            case "double":
            case "boolean":
            case "char":
            case "character":
            case "bigdecimal":
            case "string":
                simple = true;
                break;

            case "localdatetime":
            case "localdate":
                simple = true;
                break;
            default:
                simple = false;
                break;
        }

        return simple;
    }
}
