package com.langyastudio.springboot.config;


import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * spring mvc config
 */
@ConfigurationPropertiesScan("com.langyastudio.springboot.common.*")
@Configuration
public class WebConfig implements WebMvcConfigurer
{

}
