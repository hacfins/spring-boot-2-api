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
    * 消息通知
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmsMessage {

    /**
     * 机构、教室号、班级、用户
     */
    public static final byte TYPE_SCHOOL  = 1;
    public static final byte TYPE_ROOM    = 2;
    public static final byte TYPE_CLASSES = 3;
    public static final byte TYPE_USER    = 4;

    /**
     * 订阅、评论、点赞、通知
     */
    public static final byte QUEUE_SUB  = 1;
    public static final byte QUEUE_CHAT = 2;
    public static final byte QUEUE_UPVOTE = 3;
    public static final byte QUEUE_NOTICE = 4;

    private Integer id;

    /**
    * 消息id号
    */
    private String msgId;

    /**
    * 发送消息者id号
    */
    private String sendId;

    /**
    * 发送消息者类型（机构、教室号、班级、用户）
    */
    private Integer sendType;

    /**
    * 接收消息者id号
    */
    private String toId;

    /**
    * 接收消息者（机构、教室号、班级、用户）
    */
    private Integer toType;

    /**
    * 事件id号（消息触发者）
    */
    private String eventId;

    /**
    * 消息类型（订阅、评论、点赞、通知等）
    */
    private Integer queue;

    /**
    * 消息标题
    */
    private String msgTitle;

    /**
    * 消息内容(没有时，为空)
    */
    private String msgContent;

    /**
    * 未读、已读
    */
    private Byte status;

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}