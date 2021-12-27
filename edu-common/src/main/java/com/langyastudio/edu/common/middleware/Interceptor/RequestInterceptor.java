package com.langyastudio.edu.common.middleware.Interceptor;

import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.exception.MyException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求拦截器
 */
@Component
public class RequestInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws MyException
    {
        String method = request.getMethod();
        if (method != null && (!"GET".equals(method) && !"POST".equals(method)))
        {
            throw new MyException(EC.ERROR_REQUEST_METHOD_NOT_SUPPORT.getCode(), "仅支持GET和POST请求");
        }

        String contentType = request.getContentType();
        if (!"GET".equals(method) && contentType != null && (!contentType.contains("application/json") &&
                !contentType.contains("multipart/form-data")))
        {
            throw new MyException(EC.ERROR_REQUEST_METHOD_NOT_SUPPORT.getCode(),"POST请求仅支持application/json和multipart/form-data");
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception
    {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception
    {

    }
}
