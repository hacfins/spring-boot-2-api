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
 * 省市区表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmsDivision
{
    public static final int TOP_NODE_ID = 0;

    /**
     * 省市区id号
     */
    @TableId(type = IdType.AUTO, value = "pcd_id")
    private Integer pcdId;

    /**
     * 名称
     */
    private String pcdName;

    /**
     * 路径（A/B/C）
     */
    private String pcdPath;

    /**
     * 父节点id号
     */
    private Integer parentId;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 更新时间
     */
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    private LocalDateTime deleteTime;

    /**
     * 创建时间
     */
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}