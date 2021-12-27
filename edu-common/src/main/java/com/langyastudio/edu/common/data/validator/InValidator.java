package com.langyastudio.edu.common.data.validator;

import com.langyastudio.edu.common.anno.InValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 状态值是否是指定值的校验器
 */
public class InValidator implements ConstraintValidator<InValue, Object>
{
    private String[] values;

    @Override
    public void initialize(InValue flagValidator)
    {
        this.values = flagValidator.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext)
    {
        boolean isValid = false;

        if (value == null)
        {
            //当状态为空时使用默认值
            return true;
        }

        for (String s : values)
        {
            if (s.equals(String.valueOf(value)))
            {
                isValid = true;
                break;
            }
        }

        return isValid;
    }
}
