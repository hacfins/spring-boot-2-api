package com.langyastudio.edu.portal.common.middleware;

import com.langyastudio.edu.common.exception.MyException;
import com.langyastudio.edu.common.middleware.Interceptor.RequestInterceptor;
import com.langyastudio.edu.common.util.Tool;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 请求拦截器
 */
@Component
@RequiredArgsConstructor
public class LimitInterceptor extends RequestInterceptor
{
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws MyException
    {

        String ip          = Tool.getClientIp(request);
        int    limitPeriod = 60;
        int    limitCount  = 100;
        String keys        = "limit_" + ip;

        String            luaScript   = buildLuaScript();
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        Long count = redisTemplate.execute(redisScript, List.of(keys), limitCount,
                                           limitPeriod);
        if (count != null && count.intValue() <= limitCount)
        {

        }
        else
        {
            throw new MyException("接口访问超出频率限制");
        }

        return super.preHandle(request, response, handler);
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
