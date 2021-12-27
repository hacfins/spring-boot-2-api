package com.langyastudio.edu.common.data.validator;

import cn.hutool.core.util.StrUtil;
import com.langyastudio.edu.common.anno.PhoneOrEmpty;
import com.langyastudio.edu.common.data.BasDefine;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 手机号 or empty
 *
 * @author langyastudio
 */
public class PhoneOrEmptyValidator implements ConstraintValidator<PhoneOrEmpty, String>
{
    @Override
    public void initialize(PhoneOrEmpty flagValidator)
    {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext)
    {
        if (StrUtil.isNotBlank(value))
        {
            if (value.matches(BasDefine.PATTERN_PHONE))
            {
                return true;
            }

            return false;
        }

        //当状态为空时使用默认值
        return true;
    }
}
