package com.langyastudio.edu.admin.common.task;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
@Log4j2
public class AsyncTask
{
    /**
     * 使用自定义的 ThreadPoolTaskExecutor
     * @param i
     */
    @Async("customTaskExecutor")
    public ListenableFuture<String> loopPrint(Integer i)
    {
        String res = "async task:" + i;
        log.info(res);
        return new AsyncResult<>(res);
    }
}
