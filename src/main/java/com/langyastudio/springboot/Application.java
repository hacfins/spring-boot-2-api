package com.langyastudio.springboot;

import com.langyastudio.springboot.common.middleware.task.AsyncTask;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 应用程序入口
 */
@SpringBootApplication(scanBasePackages = {"com.langyastudio.springboot"})
@EnableScheduling
@EnableAsync
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication app = new SpringApplication(Application.class);

        //app.setBannerMode(Banner.Mode.OFF);
        //app.addListeners(new MyListener());

        app.run(args);
    }

    @Bean
    CommandLineRunner asyncTaskClr(AsyncTask asyncTask)
    {
        return (args) -> {
            for (int ix=0; ix<10; ix++)
            {
                asyncTask.loopPrint(ix);
            }
        };
    }

    /**
     * 自定义异步多线程的 ThreadPoolTaskExecutor
     * @param builder TaskExecutorBuilder
     * @return
     */
    @Bean
    ThreadPoolTaskExecutor customTaskExecutor(TaskExecutorBuilder builder)
    {
        return builder.threadNamePrefix("langyatask")
                .corePoolSize(5)
                .maxPoolSize(10)
                .build();
    }
}
