package com.langyastudio.edu.common.entity;

import lombok.Data;

import java.util.Map;

/**
 * 操作日志
 */
@Data
public class WebLog
{
    /**
     * 扩展信息
     */
    private String schoolId;

    /**
     * 扩展信息
     */
    private String userName;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作时间
     */
    private Long startTime;

    /**
     * 消耗时间
     */
    private Integer spendTime;

    /**
     * 根路径
     */
    private String basePath;

    /**
     * URI
     */
    private String uri;

    /**
     * URL
     */
    private String url;

    /**
     * agent
     */
    private UserAgentData userAgent;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求参数
     */
    private Map<String, Object> parameter;

    /**
     * 请求返回的结果
     */
    private Object result;
}
