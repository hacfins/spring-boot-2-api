package com.langyastudio.springboot.common.middleware.monitor;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * actuator health
 * @author langyastudio
 * @date 2021年08月13日
 */
@Component
public class MonitorHealthIndicator implements HealthIndicator
{
    @Override
    public Health health()
    {
        //Health.down();
       return  Health.up()
                .withDetail("error", "very good")
                .build();
    }
}
