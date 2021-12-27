package com.langyastudio.edu.db.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmsUser
{
    /**
     * 账户
     */
    public final static byte USER_TYPE_USER = 1;
    /**
     * 教室号账户
     */
    public final static byte USER_TYPE_ROOM = 2;


    /**
     * 未知性别
     */
    public final static String USER_SEX_UNKNOWN    = "3";
    public final static Integer USER_SEX_UNKNOWN_I = 3;

    /**
     * admin 用户名
     */
    public final static String USER_ADMIN_NAME = "admin";

    @JSONField(serialize = false)
    @TableId(type = IdType.AUTO)
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
     * 账户类型
     */
    private Byte userType;

    /**
     * 更新时间
     */
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}