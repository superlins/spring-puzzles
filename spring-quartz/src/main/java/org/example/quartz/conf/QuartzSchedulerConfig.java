package org.example.quartz.conf;

import org.quartz.JobListener;
import org.quartz.SchedulerListener;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author renc
 */
@Configuration(proxyBeanMethods = false)
public class QuartzSchedulerConfig {

    @Bean
    SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer(
            ObjectProvider<SchedulerListener> schedulerListeners,
            ObjectProvider<JobListener> jobListeners,
            ObjectProvider<TriggerListener> triggerListeners
    ) {
        return schedulerFactoryBean -> {
            schedulerListeners.stream().forEach(schedulerFactoryBean::setSchedulerListeners);
            jobListeners.stream().forEach(schedulerFactoryBean::setGlobalJobListeners);
            triggerListeners.stream().forEach(schedulerFactoryBean::setGlobalTriggerListeners);
            // schedulerFactoryBean.setTaskExecutor(new SimpleThreadPoolTaskExecutor());
        };
    }
}
