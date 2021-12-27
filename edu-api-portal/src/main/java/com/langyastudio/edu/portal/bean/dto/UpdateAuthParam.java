package com.langyastudio.edu.portal.bean.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * 校验码参数
 * 例如：更新手机号、一键登录or注册
 */
@Data
public class UpdateAuthParam
{
    /**
     * 用户名
     */
    @Size(min = 2, max = 20)
    private String userName;

    /**
     * 手机号 or 邮箱
     */
    @NotEmpty
    private String name;

    /**
     * 校验码
     */
    @NotEmpty
    private String verifyCode;
}
