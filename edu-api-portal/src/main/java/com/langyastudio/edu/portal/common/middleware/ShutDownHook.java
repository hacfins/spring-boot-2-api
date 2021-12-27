package com.langyastudio.edu.portal.common.middleware;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * 退出进程
 */
@Log4j2
@Component
public class ShutDownHook
{
    @EventListener(classes = {ContextClosedEvent.class})
    public void onApplicationClosed(@NonNull ApplicationEvent event)
    {
        log.info("前台-系统已关闭");
    }
}
