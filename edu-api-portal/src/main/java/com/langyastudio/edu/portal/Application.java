package com.langyastudio.edu.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 前台入口文件
 */
@SpringBootApplication(scanBasePackages = {"com.langyastudio.edu.*"})
@EnableScheduling
@EnableRedisHttpSession
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
}
