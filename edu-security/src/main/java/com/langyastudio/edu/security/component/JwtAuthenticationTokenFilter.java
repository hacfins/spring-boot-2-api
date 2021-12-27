package com.langyastudio.edu.security.component;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.langyastudio.edu.common.data.wrapper.RequestWrapper;
import com.langyastudio.edu.security.util.JwtTokenUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * JWT登录授权过滤器
 */
@Log4j2
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter
{
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil       jwtTokenUtil;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException
    {
        //认证信息从Header 或 请求参数 中获取
        String authHeader = request.getHeader(this.tokenHeader);
        if (Objects.isNull(authHeader))
        {
            authHeader = request.getParameter(this.tokenHeader);
        }

        if (Objects.nonNull(authHeader) && authHeader.startsWith(this.tokenHead))
        {
            // The part after "Bearer "
            String authToken = authHeader.substring(this.tokenHead.length());
            String username  = jwtTokenUtil.getUserNameFromToken(authToken);

            //checking username
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
            {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(authToken, userDetails))
                {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource()
                                                      .buildDetails(request));

                    request.setAttribute("user_name", username);

                    // authenticated username
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
