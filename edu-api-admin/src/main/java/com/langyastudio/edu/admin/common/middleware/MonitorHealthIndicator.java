package com.langyastudio.edu.admin.common.middleware;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * actuator health
 * @author langyastudio
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
