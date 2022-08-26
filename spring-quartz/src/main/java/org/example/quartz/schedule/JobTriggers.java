package org.example.quartz.schedule;

import org.example.quartz.schedule.job.QuartzJob;
import org.quartz.*;

import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;
import static org.quartz.impl.matchers.GroupMatcher.groupEquals;

/**
 * @author renc
 */
public final class JobTriggers {

    private Scheduler sched;

    public JobTriggers(Scheduler sched) {
        this.sched = sched;
    }

    // List Triggers of Job
    List<? extends Trigger> listTriggersOfJob() throws SchedulerException {
        return sched.getTriggersOfJob(jobKey("jobName", "jobGroup"));
    }

    // Listing Triggers In Scheduler
    void listTriggers() throws SchedulerException {
        // enumerate each trigger group
        for(String group: sched.getTriggerGroupNames()) {
            // enumerate each trigger in group
            for(TriggerKey triggerKey : sched.getTriggerKeys(groupEquals(group))) {
                System.out.println("Found trigger identified by: " + triggerKey);
            }
        }
    }

    // Listing Jobs in the Scheduler
    void listJobs() throws SchedulerException {
        // enumerate each job group
        for(String group: sched.getJobGroupNames()) {
            // enumerate each job in group
            for(JobKey jobKey : sched.getJobKeys(groupEquals(group))) {
                System.out.println("Found job identified by: " + jobKey);
            }
        }
    }

    // Define a new Trigger
    void updateExistTrigger() throws SchedulerException {
        Trigger trigger = newTrigger()
                .withIdentity("newTrigger", "group1")
                .startNow()
                .build();

        // tell the scheduler to remove the old trigger with the given key, and put the new one in its place
        sched.rescheduleJob(triggerKey("oldTrigger", "group1"), trigger);

        // ------ OR ------

        // retrieve the trigger
        Trigger oldTrigger = sched.getTrigger(triggerKey("oldTrigger", "group1"));

        // obtain a builder that would produce the trigger
        TriggerBuilder tb = oldTrigger.getTriggerBuilder();

        // update the schedule associated with the builder, and build the new trigger
        // (other builder methods could be called, to change the trigger in any desired way)
        Trigger newTrigger = tb.withSchedule(
                simpleSchedule().withIntervalInSeconds(10).withRepeatCount(10)
        ).build();

        sched.rescheduleJob(oldTrigger.getKey(), newTrigger);
    }

    // Add the new job to the scheduler, instructing it to "replace"
    //  the existing job with the given name and group (if any)
    void updateExistJob() throws SchedulerException {
        JobDetail job1 = newJob(QuartzJob.class)
                .withIdentity("job1", "group1")
                .usingJobData("key", "value")
                .build();

        // store, and set overwrite flag to 'true'
        sched.addJob(job1, true);
    }

    // Define a Trigger that will fire "now" and associate it with the existing job
    void scheduleAlreadyStoredJob() throws SchedulerException {
        Trigger trigger = newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .forJob(jobKey("job1", "group1"))
                .build();

        // Schedule the trigger
        sched.scheduleJob(trigger);
    }

    // Define a durable job instance (durable jobs can exist without triggers)
    void addJob() throws SchedulerException {
        JobDetail job1 = newJob(QuartzJob.class)
                .withIdentity("job1", "group1")
                .storeDurably()
                .build();

        // Add the job to the scheduler's store
        sched.addJob(job1, false);
    }

    // Delete the identified Job from the Scheduler - and any associated Triggers
    boolean deleteJob() throws SchedulerException {
        return sched.deleteJob(jobKey("job1", "group1"));
    }

    // If the related job does not have any other triggers, and the job is
    // not durable, then the job will also be deleted
    boolean unScheduleJob() throws SchedulerException {
        return sched.unscheduleJob(triggerKey("trigger1", "group1"));
    }

    // Define a Trigger
    public static Trigger defineTrigger() {
        Trigger trigger;

        trigger = newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                // .withSchedule(calendarIntervalSchedule()
                //         .withIntervalInMinutes(60))
                .withSchedule(simpleSchedule().withIntervalInSeconds(10).repeatForever())
                .build();

        // trigger = newTrigger()
        //         .withIdentity("trigger", "group")
        //         .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * 8-17 26-5 8-9 ? 2021"))
        //         .build();

        return trigger;
    }

    // Define a JobDetail
    public static JobDetail defineJob() {
        return newJob(QuartzJob.class)
                .withIdentity("job1", "group1")
                .usingJobData("string-key", "someValue")
                .usingJobData("long-key", 1000L)
                .usingJobData("name", "test-name")
                .build();
    }
}
