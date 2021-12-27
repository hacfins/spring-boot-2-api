package com.langyastudio.springboot.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 本地授权信息表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmsUserAuth {
    private Integer id;

    /**
    * 用户名
    */
    private String userName;

    /**
    * 密码
    */
    private String pwd;

    /**
    * 手机号码
    */
    private String phone;

    /**
    * 邮箱
    */
    private String email;

    /**
    * 状态
    */
    private Byte enabled;

    /**
    * 锁
    */
    private Byte locked;

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