package com.langyastudio.springboot.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 用户信息表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmsUser {
    private Integer id;

    /**
    * 用户名
    */
    private String userName;

    /**
    * 昵称
    */
    private String nickName;

    /**
    * 姓名
    */
    private String fullName;

    /**
    * 性别
    */
    private Byte sex;

    /**
    * 用户头像
    */
    private String avator;

    /**
    * 行政区划代码
    */
    private Integer pcdId;

    /**
    * 联系地址
    */
    private String company;

    /**
    * 生日
    */
    private LocalDate birthday;

    /**
    * 描述信息
    */
    private String description;

    /**
    * 注册IP
    */
    private Long regIp;

    /**
    * 用户类型（1 账户、2 设备账户）
    */
    private Byte userType;

    /**
    * 更新时间
    */
    private LocalDateTime updateTime;

    /**
    * 删除时间
    */
    private LocalDateTime deleteTime;

    /**
    * 创建时间
    */
    private LocalDateTime createTime;
}