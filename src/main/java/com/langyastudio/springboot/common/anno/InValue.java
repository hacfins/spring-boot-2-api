package com.langyastudio.springboot.common.anno;

import com.langyastudio.springboot.common.data.validator.InValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 状态值是否在指定范围内
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Constraint(validatedBy = InValidator.class)
public @interface InValue
{
    String[] value() default {"1", "2"};

    String message() default "参数值异常";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
