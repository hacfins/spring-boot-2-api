package com.langyastudio.edu.db.mapper;

import com.langyastudio.edu.common.data.PageIn;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.langyastudio.edu.db.model.UmsLogs;

/**
 * 操作日志
 * 逻辑Id号 - opId
 */
public interface UmsLogsMapper extends Mapper<UmsLogs>
{
    /**
     * 插入日志记录
     *
     * @param record 日志
     *
     * @return
     */
    int insertLog(UmsLogs record);

    /**
     * 日志列表
     *
     * @param schoolId      机构id号
     * @param likeSgPaths        分校
     * @param minCreateTime 开始时间
     * @param maxCreateTime 结束时间
     * @param likeUserName  用户名检索关键字
     * @param likeFullName  姓名检索关键字
     * @param likePhone     手机号检索关键字
     * @param userType      用户类型
     * @param range         分页
     *
     * @return
     */
    PageIn<UmsLogs> getListBySchoolId(@Param("schoolId") String schoolId,
                                      @Param("likeSgPaths") List<String> likeSgPaths,
                                      @Param("minCreateTime") LocalDateTime minCreateTime,
                                      @Param("maxCreateTime") LocalDateTime maxCreateTime,
                                      @Param("likeUserName") String likeUserName,
                                      @Param("likeFullName") String likeFullName,
                                      @Param("likePhone") String likePhone,
                                      @Param("userType") Byte userType,
                                      PageIn<?> range);
}