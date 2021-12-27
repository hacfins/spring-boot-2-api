package com.langyastudio.edu.portal.bean.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * 一键登录or注册
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LoginVerifyParam
{
    /**
     * 手机号 or 邮箱
     */
    @NotEmpty
    private String name;

    /**
     * 密码
     */
    @NotEmpty
    private String verifyCode;
}
