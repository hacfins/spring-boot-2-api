package com.langyastudio.springboot.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Mybatis Plus
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.langyastudio.springboot.mapper"})
public class MybatisConfig
{

}