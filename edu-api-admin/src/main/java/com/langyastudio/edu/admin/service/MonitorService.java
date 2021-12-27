package com.langyastudio.edu.admin.service;

import com.langyastudio.edu.db.model.UmsUserLoginLogs;
import com.langyastudio.edu.common.data.PageInfo;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 监控服务 - 日志、监控、系统信息等
 */
public interface MonitorService extends BaseService
{
    /*-------------------------------------------------------------------------------------------------------------- */
    // 日志
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 获取登录日志列表
     *
     * @param userName 用户名
     * @param offSet   offSet
     * @param pageSize pageSize
     *
     * @return
     */
    PageInfo<UmsUserLoginLogs> getLogLoginList(String userName, Integer offSet, Integer pageSize);

    /**
     * 获取系统日志列表
     *
     * @param userNameKey 用户名关键字
     * @param fullNameKey 姓名关键字
     * @param phoneKey    电话号码关键字
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param offSet      offset
     * @param pageSize    pagesize
     *
     * @return
     */
    PageInfo<Map<String, Object>> getLogSystemList(String userNameKey, String fullNameKey, String phoneKey,
                                                   LocalDateTime startTime, LocalDateTime endTime,
                                                   Integer offSet, Integer pageSize);

    /*-------------------------------------------------------------------------------------------------------------- */
    // 系统信息
    /*-------------------------------------------------------------------------------------------------------------- */



    /*-------------------------------------------------------------------------------------------------------------- */
    // 监控
    /*-------------------------------------------------------------------------------------------------------------- */

    /**
     * 用户活跃度
     *
     * @param schoolId
     * @param startTime
     * @param endTime
     * @param offSet
     * @param pageSize
     * @return
     */
    PageInfo<Map<String, Object>> getLogUserList(String schoolId, LocalDateTime startTime, LocalDateTime endTime,
                                                   Integer offSet, Integer pageSize);
}
