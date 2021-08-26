package org.example.quartz.service;

import org.example.quartz.schedule.job.QuartzJob;
import org.quartz.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * @author renc
 */
@Component
public class JobAcceptorService implements CommandLineRunner {

    private final Scheduler sched;

    public JobAcceptorService(Scheduler scheduler) {
        this.sched = scheduler;
    }

    @Override
    public void run(String... args) throws Exception {
        sched.scheduleJob(defineJob(), defineTrigger());
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
    Trigger defineTrigger() {
        Trigger trigger;

        trigger = newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .build();

        trigger = newTrigger()
                .withIdentity("trigger", "group")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * 8-17 26-5 8-9 ? 2021"))
                .build();

        return trigger;
    }

    // Define a JobDetail
    JobDetail defineJob() {
        return newJob(QuartzJob.class)
                .withIdentity("job1", "group1")
                .usingJobData("someProp", "someValue")
                .build();
    }
}
