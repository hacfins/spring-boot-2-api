package com.langyastudio.edu.common.service;

import com.langyastudio.edu.common.entity.WebLog;

/**
 * 日志服务
 */
public interface LogService
{
    /**
     * 写入系统日志
     * @param webLog
     */
    void addLog(WebLog webLog);
}
