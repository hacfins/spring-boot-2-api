package com.langyastudio.edu.db.model;

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
 * 本地授权信息表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmsUserAuth
{
    /**
     * 可用
     */
    public static final  byte USER_AUTH_ENABLED_YES = 1;
    /**
     * 不可用
     */
    public static final byte USER_AUTH_ENABLED_NO = 2;

    /**
     * 未知性别
     */
    public static final String USER_AUTH_PWD = "123456a";

    @JSONField(serialize = false)
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    @JSONField(serialize = false)
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
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除时间
     */
    @JSONField(serialize = false)
    private LocalDateTime deleteTime;

    /**
     * 创建时间
     */
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}