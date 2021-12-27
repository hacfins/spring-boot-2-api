package com.langyastudio.edu.common.anno;

import com.langyastudio.edu.common.entity.DesensitizationType;
import org.apache.logging.log4j.util.Strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脱敏
 *
 * @author langyastudio
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Desensitization
{
    /**
     * 脱敏规则类型
     */
    DesensitizationType type();

    /**
     * 附加值, 自定义正则表达式等
     */
    String[] attach() default Strings.EMPTY;
}
