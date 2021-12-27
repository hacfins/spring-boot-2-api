package com.langyastudio.edu.security.component;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.langyastudio.edu.common.data.EC;
import com.langyastudio.edu.common.data.ResultInfo;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 自定义返回结果：未登录或登录过期
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint
{
    private static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_\\.]*");

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException
    {
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");

        ResultInfo resultInfo = ResultInfo.data(EC.ERROR_USER_UNAUTHORIZED, authException.getMessage());

        //兼容jsonp请求
        String functionName = request.getParameter("callback");
        if (functionName != null && this.isValidJsonpQueryParam(functionName))
        {
            String text      = JSON.toJSONString(resultInfo);
            String jsonpText = functionName + "(" + text + ")";

            //jsonp content
            response.setStatus(200);
            response.setContentType("application/javascript");
            response.getWriter().print(jsonpText);
        }
        else
        {
            // 跨域
            String uiDomain = null;
            String origin   = request.getHeader("origin");
            if (StrUtil.isNotBlank(origin))
            {
                uiDomain = origin;
            }

            if (StrUtil.isNotBlank(uiDomain))
            {
                String method = request.getMethod();
                if ("OPTIONS".equals(method) || "POST".equals(method))
                {
                    response.addHeader("Access-Control-Allow-Origin", uiDomain);
                    response.addHeader("Access-Control-Allow-Credentials", "true");

                    // 在这个时间范围内，所有同类型的请求都将不再发送预检请求而是直接使用此次返回的头作为判断依据
                    response.addHeader("Access-Control-Max-Age", "1440");
                }
            }

            //返回数据
            response.setContentType("application/json");
            response.getWriter().println(JSONUtil.parse(resultInfo));
        }

        response.getWriter().flush();
    }

    protected boolean isValidJsonpQueryParam(String value)
    {
        return CALLBACK_PARAM_PATTERN.matcher(value).matches();
    }
}
