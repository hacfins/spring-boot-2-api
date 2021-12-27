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
 * 角色权限映射表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmsRoleApis
{
    @JSONField(serialize = false)
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 角色id号
     */
    private String roleId;

    /**
     * 权限id号
     */
    private String apiId;

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