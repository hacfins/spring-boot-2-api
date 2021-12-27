package com.langyastudio.springboot.common.middleware.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * jsonp请求
 */
@ControllerAdvice
public class JsonpResponseAdvice extends FastJsonHttpMessageConverter implements ResponseBodyAdvice
{
    private static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_\\.]*");
    public static final  Charset UTF8                   = StandardCharsets.UTF_8;

    private final Charset             charset;
    private final SerializerFeature[] features;

    @Override
    public Object beforeBodyWrite(Object obj,
                                  @NotNull MethodParameter methodParameter,
                                  @NotNull MediaType mediaType,
                                  @NotNull Class aClass,
                                  @NotNull ServerHttpRequest serverHttpRequest,
                                  @NotNull ServerHttpResponse serverHttpResponse)
    {

        HttpServletRequest  servletRequest = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
        HttpServletResponse response       = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();
        String              functionName   = servletRequest.getParameter("callback");

        if (functionName != null)
        {
            if (this.isValidJsonpQueryParam(functionName))
            {
                String text      = JSON.toJSONString(obj, this.features);
                String jsonpText = functionName + "(" + text + ")";

                byte[]       bytes = jsonpText.getBytes(this.charset);
                OutputStream out   = null;
                try
                {
                    //jsonp content
                    response.setContentType("application/javascript; charset=utf-8");

                    out = response.getOutputStream();
                    out.write(bytes);
                    out.flush();
                    out.close();
                }
                catch (IOException e)
                {

                }
            }
        }

        return obj;
    }


    @Override
    public boolean supports(@NotNull MethodParameter methodParameter,
                            @NotNull Class aClass)
    {
        return true;
    }

    protected boolean isValidJsonpQueryParam(String value)
    {
        return CALLBACK_PARAM_PATTERN.matcher(value).matches();
    }

    public JsonpResponseAdvice()
    {
        super();
        this.charset = UTF8;
        this.features = new SerializerFeature[0];
    }
}