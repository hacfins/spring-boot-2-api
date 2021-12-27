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
 * 操作日志表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmsLogs
{
    @JSONField(serialize = false)
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 操作id号
     */
    private String opId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 机构id号（系统也理解为一个机构）
     */
    @JSONField(serialize = false)
    private String schoolId;

    /**
     * 操作的url（请求的URL地址）
     */
    private String opUrl;

    /**
     * 操作说明
     */
    private String opComment;

    /**
     * 操作的参数
     */
    private String opParams;

    /**
     * 操作结果（操作成功/操作失败/操作异常等）
     */
    private Integer opResult;

    /**
     * 耗时（微秒）
     */
    private Integer useTime;

    /**
     * 操作地点（济南）
     */
    private String location;

    /**
     * 操作ip
     */
    private Long ip;

    /**
     * 操作系统（Windows 10, MacOS 10）
     */
    private String osName;

    /**
     * 浏览器（如 Chrome 87）
     */
    private String browseName;

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
     * 操作时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}