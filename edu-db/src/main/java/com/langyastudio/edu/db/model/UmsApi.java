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
 * 权限表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmsApi
{
    @JSONField(serialize = false)
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 是否拥有该权限
     */
    private Integer status;

    /**
     * 权限id号
     */
    private String apiId;

    /**
     * 名称
     */
    private String apiName;

    /**
     * 模块组
     */
    private String apiModule;

    /**
     * 地址
     */
    private String apiUrl;

    /**
     * 更新时间
     */
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记
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