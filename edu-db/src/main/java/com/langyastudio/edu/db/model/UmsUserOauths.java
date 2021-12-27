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
 * 第三方授权信息表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmsUserOauths
{
    @JSONField(serialize = false)
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 第三方应用的唯一标识
     */
    private String oauthId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 第三方应用类型
     */
    private Byte oauthType;

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
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}