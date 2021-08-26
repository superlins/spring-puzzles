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

        TriggerKey triggerKey = new TriggerKey("trigger", "group");
        JobKey jobKey = new JobKey("job", "group");
        // try {
        //     System.out.println("unscheduleJob");
        //     scheduler.unscheduleJob(triggerKey);
        // } catch (SchedulerException e) {
        //     e.printStackTrace();
        // }

        try {
            boolean b = scheduler.deleteJob(jobKey);
            System.out.println("delete job: " + b);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        // try {
        //     System.out.println("rescheduleJob");
        //     Date date = scheduler.rescheduleJob(triggerKey, trigger);
        // } catch (SchedulerException e) {
        //     e.printStackTrace();
        // }
    }
}
