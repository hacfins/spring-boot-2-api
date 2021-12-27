package com.langyastudio.springboot.common.middleware.task;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 计划任务
 *
 * @author langyastudio
 * @date 2021年08月16日
 */
@Log4j2
@Component
public class SchedulingTask
{
    @Scheduled(fixedRate = 60000)
    public void  fixedRateSchedule()
    {
        log.info("每 60 秒钟执行fixedRate...");
    }


    /**
     * initialDelay 延迟 5 秒后开始执行该任务
     */
    @Scheduled(initialDelay = 5000, fixedDelayString = "${langyastudio.task.fixedDelaySchedule:10000}")
    public void  fixedDelaySchedule()
    {
        log.info("在上次任务完成 10 秒后执行fixedDelay...");
    }

    /**
     * cron 表达式
     */
    @Scheduled(cron = "*/30 * * * * *")
    public void  cronchedule()
    {
        log.info("每30秒执行cron...");
    }
}
