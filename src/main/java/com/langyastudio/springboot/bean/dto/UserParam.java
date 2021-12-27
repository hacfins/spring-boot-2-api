package com.langyastudio.springboot.bean.dto;

import com.langyastudio.springboot.common.data.validator.InsertV;
import com.langyastudio.springboot.common.data.validator.UpdateV;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * user 传入参数
 */
@Data
public class UserParam
{
    @Null(message = "新增时id必须为空", groups = {InsertV.class})
    @NotNull(message = "更新时id不能为空", groups = {UpdateV.class})
    @Positive
    private Integer id;

    /**
     * 长度2-20
     * 不能为null
     */
    @NotNull
    @Size(min=2, max = 20)
    private String userName;

    /**
     * 长度2-20
     */
    @Size(min=2, max = 20)
    private String nickName;

    /**
     * 长度2-20
     * 邮箱格式
     */
    @NotNull
    @Email(message = "邮箱格式有误")
    private String email;

    /**
     * 手机号正则匹配
     */
    @Pattern(regexp = "^1[3,4,5,6,7,8,9]{1}[0-9]{9}$")
    private String telephone;
}