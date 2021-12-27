package com.langyastudio.edu.common.middleware.filter;

import javax.servlet.*;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Cors 跨域
 *
 * @author jiangjiaxiong
 */
@Order(1)
@Component
public class CorsFilterBean extends FilterRegistrationBean<Filter>
{
    @PostConstruct
    public void init()
    {
        setFilter(new CorsFilter());
        setUrlPatterns(List.of("/admin/*", "/portal/*"));
    }

    class CorsFilter implements Filter
    {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException
        {
            HttpServletRequest  req  = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;

            // 跨域
            String uiDomain = null;
            String origin   = req.getHeader("origin");
            if (StrUtil.isNotBlank(origin))
            {
                uiDomain = origin;
            }

            if (StrUtil.isNotBlank(uiDomain))
            {
                String method = req.getMethod();

                if ("OPTIONS".equals(method) || "POST".equals(method))
                {
                    resp.addHeader("Access-Control-Allow-Origin", uiDomain);
                    resp.addHeader("Access-Control-Allow-Headers", "access-control-allow-origin," +
                            "Authorization," + "school_id," +
                            "token,Content-Type,Cookie,Origin, Referer,If-Match, If-Modified-Since, If-None-Match," +
                            "If-Unmodified-Since, X-Requested-With,X_Requested_With");
                    resp.addHeader("Access-Control-Allow-Method", "GET, POST");
                    resp.addHeader("Access-Control-Allow-Credentials", "true");

                    // 在这个时间范围内，所有同类型的请求都将不再发送预检请求而是直接使用此次返回的头作为判断依据
                    resp.addHeader("Access-Control-Max-Age", "1440");
                }

                //P3P
                //跨域向IE写入cookie
                resp.addHeader("P3P", "CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI " +
                        "DSP COR\"");

                // OPTIONS请求
                if ("OPTIONS".equals(method))
                {
                    //204 empty
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                }
            }
            resp.addHeader("Keep-Alive", "timeout=5, max=60");

            chain.doFilter(request, response);
        }
    }
}
