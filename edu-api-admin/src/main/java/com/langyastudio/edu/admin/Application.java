package com.langyastudio.edu.admin;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 后台入口文件
 */
@SpringBootApplication(scanBasePackages = {"com.langyastudio.edu.*"})
@EnableAsync
@EnableScheduling
@EnableRedisHttpSession
public class Application
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder(Application.class)
                .beanNameGenerator(new UniqueNameGenerator())
                .run(args);

        //SpringApplication.run(Application.class, args);
    }


    /**
     * 由于需要混淆代码,混淆后类都是A B C,spring 默认是把A B C当成BeanName,BeanName又不能重复导致报错
     * 所以需要重新定义BeanName生成策略
     * 不能重写generateBeanName方法,因为有些Bean会自定义BeanName,所以这些情况还需要走原来的逻辑
     */
    @Component("UniqueNameGenerator")
    public static class UniqueNameGenerator extends AnnotationBeanNameGenerator
    {
        /**
         * 重写buildDefaultBeanName
         * 其他情况(如自定义BeanName)还是按原来的生成策略,只修改默认(非其他情况)生成的BeanName带上包名
         */
        @Override
        public @NotNull String buildDefaultBeanName(BeanDefinition definition)
        {
            //全限定类名
            return Objects.requireNonNull(definition.getBeanClassName());
        }
    }
}
