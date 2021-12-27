package com.langyastudio.edu.common.entity;

import lombok.Data;

@Data
public class UserAgentData
{
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
}
