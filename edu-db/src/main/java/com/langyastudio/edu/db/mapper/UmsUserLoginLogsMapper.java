package com.langyastudio.edu.db.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.langyastudio.edu.common.data.PageIn;
import org.apache.ibatis.annotations.Param;

import com.langyastudio.edu.db.model.UmsUserLoginLogs;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户登录表
 * 逻辑Id号 - id
 */
public interface UmsUserLoginLogsMapper extends Mapper<UmsUserLoginLogs>
{
    /**
     * 插入数据
     *
     * @param record 记录
     * @return
     */
    int insertUserLoginLogs(UmsUserLoginLogs record);

    /**
     * 获取登录日志表
     * @param userName 用户名
     *
     * @return
     */
    PageIn<UmsUserLoginLogs> getListByUserName(@Param("userName")String userName, PageIn<?> range);

    /**
     * 用户活跃度
     *
     * @param schoolId
     * @param minCreateTime
     * @param maxCreateTime
     * @param range
     * @return
     */
    PageIn<Map<String, Object>> getListByActive(@Param("schoolId") String schoolId,
                                                @Param("minCreateTime") LocalDateTime minCreateTime,
                                                @Param("maxCreateTime") LocalDateTime maxCreateTime,
                                                PageIn<?> range);
}