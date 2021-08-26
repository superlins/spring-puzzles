package org.example.quartz.conf;

import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author renc
 */
@Configuration(proxyBeanMethods = false)
public class QuartzSchedulerConfig {

    @Bean
    SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer() {
        return schedulerFactoryBean -> {
            // schedulerFactoryBean.setTaskExecutor(new SimpleThreadPoolTaskExecutor());
        };
    }
}
