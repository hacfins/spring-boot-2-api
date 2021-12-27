package com.langyastudio.edu.portal.bean.dto;

import com.langyastudio.edu.common.anno.InValue;
import lombok.Data;

import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 用户传入参数
 */
@Data
public class UserParam
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
