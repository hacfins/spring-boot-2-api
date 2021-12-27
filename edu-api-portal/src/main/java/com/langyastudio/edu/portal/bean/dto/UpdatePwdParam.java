package com.langyastudio.edu.portal.bean.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * 修改用户名密码参数
 */
@Data
public class UpdatePwdParam
{
    /**
     * 用户名
     */
    @Size(min = 2, max = 20)
    private String userName;

    /**
     * 旧密码
     */
    @NotEmpty
    @Size(min = 6, max = 20)
    private String oldPwd;

    /**
     * 新密码
     */
    @NotEmpty
    @Size(min = 6, max = 20)
    private String newPwd;
}
