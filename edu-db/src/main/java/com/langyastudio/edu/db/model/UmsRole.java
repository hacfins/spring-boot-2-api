package com.langyastudio.edu.db.model;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 角色表
 */
@Data
public class UmsRole
{
    /**
     * 访客角色Id号
     */
    public final static String ROLE_GUEST_ID = "f477cce7e3ae44d0999ecbafae4fd6a4";
    /**
     * 普通用户角色Id号
     */
    public final static String ROLE_USER_ID = "4ba77b3d79ff4007bbf2b6a92bde35ee";


    /**
     * 机构设备账号
     */
    public final static String ROLE_SCHOOL_DEVICE_ID = "dcfd5a42fcc74ec0b12c67d68ed8545e";
    /**
     * 机构讲师
     */
    public final static String ROLE_SCHOOL_TEACHER_ID = "a1d03c0f5838466db325e2959782ca9f";
    /**
     * 机构管理员角色Id号
     */
    public final static String ROLE_SCHOOL_ADMIN_ID   = "ed2691cb33a5494db472b79d76738aa4";

    @JSONField(serialize = false)
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 角色id号
     */
    private String roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 访问后台管理（1是、2否）
     */
    @JSONField(serialize = false)
    private Integer viewSystem;

    /**
     * 访问机构管理（1是、2否）
     */
    @JSONField(serialize = false)
    private Integer viewSchool;

    /**
     * 是否是系统类型(1，是；2，否)
     */
    private Integer isSystem;

    /**
     * 角色类型（内置角色（不可删除）、普通角色）
     */
    private Integer roleType;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 描述信息
     */
    private String description;

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