package com.langyastudio.edu.portal.bean.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * 用户登录参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LoginParam
{
    /**
     * 用户名or手机号
     */
    @NotEmpty
    @Size(min = 2, max=20)
    private String userName;

    /**
     * 密码
     */
    @NotEmpty
    @Size(min = 2, max = 20)
    private String pwd;
}
