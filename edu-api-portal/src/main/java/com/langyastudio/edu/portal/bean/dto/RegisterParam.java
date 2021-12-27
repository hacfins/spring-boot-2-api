package com.langyastudio.edu.portal.bean.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * 用户注册参数
 */
@Data
public class RegisterParam
{
    /**
     * 用户名
     */
    @NotEmpty
    private String userName;

    /**
     * 密码
     */
    @NotEmpty
    @Size(min = 6, max = 20)
    private String pwd;

    /**
     * 手机号 or 邮箱
     */
    @NotEmpty
    private String name;

    /**
     * 检验码
     */
    private String verifyCode;
}
