package com.langyastudio.edu.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

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
        SpringApplication.run(Application.class, args);
    }
}
