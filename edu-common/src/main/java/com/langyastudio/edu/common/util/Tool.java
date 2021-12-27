package com.langyastudio.edu.common.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.Ipv4Util;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.langyastudio.edu.common.entity.UserAgentData;
import com.langyastudio.edu.common.exception.MyException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 常用函数库
 */
@Component
public class Tool
{
    //------------------------------------- Common ----------------------------------------//

    /**
     * 执行命令行
     *
     * @param cmdList 命令
     * @param envp    环境
     * @param dir     执行目录
     *
     * @return
     */
    public static String exec(List<String> cmdList, String[] envp, File dir)
    {
        Process       ps     = null;
        StringBuilder output = new StringBuilder();
        try
        {
            ps = new ProcessBuilder(cmdList)
                    .directory(dir)
                    .redirectErrorStream(true)
                    .start();

            String readLine = "";
            try (BufferedReader stdout = new BufferedReader(new InputStreamReader(ps.getInputStream())))
            {
                while ((readLine = stdout.readLine()) != null)
                {
                    //output log
                    System.out.println(readLine + System.lineSeparator());

                    output.append(readLine)
                            .append(System.lineSeparator());
                }
            }

            // Waits for the command to finish.
            ps.waitFor();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (Objects.nonNull(ps))
            {
                ps.destroy();
            }
        }

        return output.toString();
    }

    /**
     * 比较两个SET是否相同
     */
    public static boolean equals(Set<?> set1, Set<?> set2)
    {
        if (set1 == null || set2 == null)
        {//null就直接不比了

            return false;
        }
        if (set1.size() != set2.size())
        {//大小不同也不用比了

            return false;
        }

        //最后比containsAll
        return set1.containsAll(set2) && set2.containsAll(set1);
    }

    //------------------------------------- Object ----------------------------------------//

    /**
     * 实体类转Map
     *
     * @param object 对象
     *
     * @return map
     */
    public static Map<String, Object> beanToMap(Object object)
    {
        //解决LocalDateTime带有T的问题
        ValueFilter valueFilter = (o, s, item) -> {
            if (null == item)
            {
                item = "";
            }
            if (item instanceof LocalDateTime)
            {
                item = ((LocalDateTime) item).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            return item;
        };
        String jsonStr = JSON.toJSONString(object, valueFilter, SerializerFeature.PrettyFormat,
                                           SerializerFeature.WriteDateUseDateFormat,
                                           SerializerFeature.WriteMapNullValue,
                                           SerializerFeature.WriteNullListAsEmpty,
                                           SerializerFeature.WriteNullStringAsEmpty);

        return JSON.parseObject(jsonStr);
    }

    /**
     * 是否所有属性都为null
     *
     * @param bean 实体类
     *
     * @return 判断结果
     *
     * @throws IllegalAccessException 非法访问异常
     */
    public static Boolean isAllFieldNull(Object bean) throws IllegalAccessException
    {
        if (null == bean)
        {
            return true;
        }

        // 得到类对象
        Class stuCla = bean.getClass();

        //得到属性集合
        Field[] fs = stuCla.getDeclaredFields();

        for (Field f : fs)
        {//遍历属性
            // 设置属性是可以访问的(私有的也可以)
            f.setAccessible(true);

            // 得到此属性的值
            Object val = f.get(bean);
            if (val != null)
            {
                //只要有1个属性不为空,那么就不是所有的属性值都为空
                return false;
            }
        }

        return true;
    }

    /**
     * @param object
     *
     * @return
     */
    public static Boolean isAllFieldEmpty(Object object)
    {
        if (null == object)
        {
            return true;
        }

        try
        {
            for (Field f : object.getClass().getDeclaredFields())
            {
                f.setAccessible(true);

                Object o = f.get(object);

                String s = o.toString();
                if (StringUtils.isEmpty(s))
                {
                    return false;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 批量删除数组元素
     *
     * @param list    要删除元素的数组
     * @param delList 要删除的元素的列表
     */
    public static <T> List<T> delArrayBat(List<T> list, List<T> delList)
    {
        List<T> sorApiIds = new ArrayList<T>(list);
        if (null != delList)
        {
            sorApiIds.removeIf(delList::contains);
        }

        return sorApiIds;
    }

    //------------------------------------- Date ----------------------------------------//

    /**
     * 日期转换为时间戳Long
     */
    public static long dateTimeToStamp(String time)
    {
        Date date = DateUtil.parse(time);

        return date.getTime();
    }

    /**
     * 时间戳转日期String
     *
     * @param time time 时间戳（毫秒）
     *
     * @return 年月日时分秒的日期
     */
    public String stampToDateStr(Long time)
    {
        return DateUtil.formatDateTime(DateUtil.date(time));
    }

    /**
     * 时间戳转Date
     *
     * @param time 时间戳（毫秒）
     *
     * @return 日期对象
     */
    public static Date stampToDateEntity(Long time)
    {
        return DateUtil.date(time);
    }

    /**
     * 日期转换为LocalDateTime
     * yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime dateTimeToEntiry(String startTime)
    {
        LocalDateTime startDateTime = null;
        if (startTime != null)
        {
            startDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        return startDateTime;
    }

    //------------------------------------- HTTP ----------------------------------------//

    /**
     * getRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest()
    {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null)
        {
            return null;
        }

        return servletRequestAttributes.getRequest();
    }

    /**
     * getResponse
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse()
    {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null)
        {
            return null;
        }

        return servletRequestAttributes.getResponse();
    }

    /**
     * 获取请求的 inputStream 数据
     *
     * @return
     */
    public static String getRequestString(HttpServletRequest request)
    {
        try
        {
            byte[]        b       = new byte[1024];
            int           lens    = -1;
            StringBuilder content = new StringBuilder();

            ServletInputStream inputStream = request.getInputStream();
            while ((lens = inputStream.read(b)) > 0)
            {
                content.append(new String(b, 0, lens));
            }

            return content.toString();
        }
        catch (IOException e)
        {
            throw new MyException(e.getMessage());
        }
    }

    /**
     * 获取param或body中的参数
     *
     * @param var1    参数名
     * @param request 不传时，将自动获取
     *
     * @return
     */
    public static String getParamEx(String var1, HttpServletRequest request)
    {
        try
        {
            if (null == request)
            {
                request = getRequest();
            }

            String method = request.getMethod();

            //get 请求从parameter中获取参数
            if ("GET".equals(method))
            {
                return request.getParameter(var1);
            }
            //post 请求从body json中获取参数
            else if ("POST".equals(method))
            {
                return (String) request.getAttribute(var1);
            }
        }
        catch (Throwable e)
        {
            throw new MyException(e.getMessage());
        }

        return null;
    }

    /**
     * 判断是否为 ajax请求
     *
     * @param request HttpServletRequest
     *
     * @return boolean
     */
    public static boolean isAjaxRequest(HttpServletRequest request)
    {
        return (request.getHeader("X-Requested-With") != null
                && "XMLHttpRequest".equals(request.getHeader("X-Requested-With")));
    }

    /**
     * 是否在win os下运行
     *
     * @return
     */
    public static boolean isWinOs()
    {
        OsInfo system = SystemUtil.getOsInfo();
        return system.isWindows();
    }

    /**
     * 获取客户端真实IP
     *
     * @return 客户端真实IP
     */
    public static String getClientIp(HttpServletRequest request)
    {
        return ServletUtil.getClientIP(request);
    }

    /**
     * 获取客户端真实IP
     *
     * @return 客户端真实IP
     */
    public static Long getClientIpLong(HttpServletRequest request)
    {
        String strIp = Tool.getClientIp(request);
        return Ipv4Util.ipv4ToLong(strIp);
    }

    /**
     * 获取user agent 信息
     *
     * @return
     */
    public static UserAgentData getUserAgentInfo(HttpServletRequest request)
    {
        if (Objects.isNull(request))
        {
            return null;
        }

        UserAgentData userAgentData = new UserAgentData();
        String        agent         = request.getHeader("User-Agent");
        UserAgent     userAgent     = UserAgentUtil.parse(agent);
        if (Objects.nonNull(userAgent))
        {
            userAgentData.setBrowseName(userAgent.getBrowser() + " " + userAgent.getVersion());

            //fixed display: Windows 10 or Windows Server 2016 as Windows 10
            String   osName  = userAgent.getOs().getName();
            String[] osNames = osName.split("or");
            userAgentData.setOsName(Objects.nonNull(osNames) ? osNames[0] : osName);
        }
        else
        {
            userAgentData.setBrowseName("Unknown");
            userAgentData.setOsName("Unknown");
        }
        String ip = getClientIp(request);
        userAgentData.setIp(Ipv4Util.ipv4ToLong(ip));
        userAgentData.setLocation(AddressT.getCityInfo(ip));

        return userAgentData;
    }

    /**
     * 判断IP在不在IP网段内
     *
     * @param ip      要查询的IP地址
     * @param network IP段 192.168.123.0/24
     *
     * @return bool
     */
    public static boolean isIpInNetwork(String ip, String network)
    {
        String[] netArr = network.split("/");
        int      size   = 32;
        if (netArr.length > 1)
        {
            size = Convert.toInt(netArr[1]);
        }

        long networkStart = Ipv4Util.ipv4ToLong(netArr[0]);
        long networkLen   = (long) Math.pow(2, 32 - size);
        long networkEnd   = (networkStart + networkLen - 1);

        long ipLong = Ipv4Util.ipv4ToLong(ip); ;
        if (ipLong >= networkStart && ipLong <= networkEnd)
        {
            return true;
        }

        return false;
    }

    /**
     * 获取Cookie
     */
    public static Map<String, Cookie> getCookies(HttpServletRequest request)
    {
        return ServletUtil.readCookieMap(request);
    }

    /**
     * 获取Cookie值
     *
     * @param cookieKey Cookie键
     */
    public static String getCookieVal(HttpServletRequest request, String cookieKey)
    {
        Map<String, Cookie> cookieMap = Tool.getCookies(request);
        if (cookieMap.containsKey(cookieKey))
        {
            return cookieMap.get(cookieKey).getValue();
        }

        return null;
    }
}
