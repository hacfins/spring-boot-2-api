package com.langyastudio.edu.portal.config;

import com.langyastudio.edu.common.config.BaseMybatisConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Mybatis Plus
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.langyastudio.edu.db.mapper")
public class MybatisConfig extends BaseMybatisConfig
{

}