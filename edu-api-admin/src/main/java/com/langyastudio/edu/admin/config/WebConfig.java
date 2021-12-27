package com.langyastudio.edu.admin.config;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.langyastudio.edu.admin.common.middleware.LimitInterceptor;
import com.langyastudio.edu.common.exception.ErrController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/**
 * spring mvc config
 */
@ConfigurationPropertiesScan("com.langyastudio.edu.admin.common.*")
@Configuration
public class WebConfig implements WebMvcConfigurer
{
    @Autowired
    private LimitInterceptor limitInterceptor;

    /**
     * resource handler
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // 映射路径`/static/`到classpath路径:
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        // 注册请求方式拦截器
        registry.addInterceptor(limitInterceptor)
                .addPathPatterns("/**")

                .excludePathPatterns(ErrController.ERROR_PATH)
                .excludePathPatterns("/favicon.ico");
    }

    /**
     * 确保参数解析优先生效
     */
//    private @Inject
//    RequestMappingHandlerAdapter adapter;
//
//    @PostConstruct
//    public void prioritizeCustomArgumentMethodHandlers()
//    {
//        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>(adapter.getArgumentResolvers());
//        List<HandlerMethodArgumentResolver> customResolvers   = adapter.getCustomArgumentResolvers();
//        argumentResolvers.removeAll(customResolvers);
//        argumentResolvers.addAll(0, customResolvers);
//        adapter.setArgumentResolvers(argumentResolvers);
//    }
//    /**
//     * 添加参数解析，将参数的形式从下划线转化为驼峰
//     */
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers)
//    {
//        argumentResolvers.add(0, new ArgumentResolver());
//    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        //1、定义一个convert转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        //2、添加fastjson的配置信息
        FastJsonConfig fastJsonConfig = fastConverter.getFastJsonConfig();

        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty);
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        fastJsonConfig.setFeatures(Feature.SupportAutoType);

        //如果时间类型值为null，则返回空串
        //否则null类型的字段不返回!!!
        ValueFilter dateFilter = (Object var1, String var2, Object var3) -> {
            try {
                if (var3 == null && var1 != null &&
                        LocalDateTime.class.isAssignableFrom(var1.getClass().getDeclaredField(var2).getType())) {
                    return "";
                }
            } catch (Exception e) {
            }
            return var3;
        };
        fastJsonConfig.setSerializeFilters(dateFilter);

        //!!下划线转驼峰
        fastJsonConfig.getParserConfig().propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        fastJsonConfig.getSerializeConfig().propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;

        //3、将convert添加到converters中
        // 指定FastJsonHttpMessageConverter在converters内的顺序
        // 否则在SpringBoot 2.0.1及之后的版本中将优先使用Jackson处理
        converters.add(0, fastConverter);
    }
}
