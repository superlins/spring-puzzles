package org.example.quartz.schedule.job;

import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author renc
 */
public class QuartzJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        Scheduler scheduler = context.getScheduler();
        Trigger trigger = context.getTrigger();

        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();

        String stringKey = jobDataMap.getString("string-key");
        Long longKey = jobDataMap.getLongValue("long-key");

        System.out.printf("[%d] >>>>> %s-%d\n", System.currentTimeMillis(), stringKey, longKey);
    }
}
