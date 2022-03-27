package com.langyastudio.edu.admin.bean.dto;

import com.langyastudio.edu.common.anno.InValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户传入参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserParam implements Serializable
{
    /**
     * 用户名
     */
    @Size(min=2, max = 20)
    private String userName;

    /**
     * 昵称
     */
    @Size(min=2, max = 20)
    private String nickName;

    /**
     * 姓名
     */
    @Size(min=2, max = 20)
    private String fullName;

    /**
     * 性别
     */
    @InValue(value = {"1", "2", "3"})
    private Byte sex;

    /**
     * 用户头像
     */
    @Size(max = 128)
    private String avator;

    /**
     * 行政区划代码
     */
    @PositiveOrZero
    private Integer pcdId;

    /**
     * 联系地址
     */
    @Size(max = 128)
    private String company;

    /**
     * 生日
     */
    @PastOrPresent
    private LocalDate birthday;

    /**
     * 描述信息
     */
    @Size(max = 128)
    private String description;
}
