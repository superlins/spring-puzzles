package org.example.quartz.schedule.job;

import org.example.quartz.service.MyService;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author renc
 */
public class QuartzJob extends QuartzJobBean {

    private MyService myService;

    private String name;

    public void setMyService(MyService myService) {
        this.myService = myService;
    }

    public void setName(String name) {
        this.name = name;
    }

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
