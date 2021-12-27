package com.langyastudio.edu.common.anno;

import com.langyastudio.edu.common.entity.LimitType;
import org.apache.logging.log4j.util.Strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API 请求限流
 *
 * @author langyastudio
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitField
{
    /**
     * 资源名称，用于描述接口功能
     */
    String name() default Strings.EMPTY;

    /**
     * 资源 key
     */
    String key() default Strings.EMPTY;

    /**
     * key prefix
     */
    String prefix() default Strings.EMPTY;

    /**
     * 时间范围，单位秒
     */
    int period() default 60;

    /**
     * 限制访问次数
     */
    int count() default 10;

    /**
     * 限制类型
     */
    LimitType limitType() default LimitType.CUSTOMER;
}
