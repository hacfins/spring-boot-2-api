package com.langyastudio.edu.portal.bean.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * 找回密码参数
 */
@Data
public class FindPwdParam
{
    /**
     * 手机号 or 邮箱
     */
    @NotEmpty
    private String name;

    /**
     * 新密码
     */
    @NotEmpty
    @Size(min = 6, max = 20)
    private String newPwd;

    /**
     * 校验码
     */
    private String verifyCode;
}
