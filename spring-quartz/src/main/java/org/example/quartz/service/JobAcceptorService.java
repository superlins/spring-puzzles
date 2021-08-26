package org.example.quartz.service;

import org.example.quartz.schedule.JobTriggers;
import org.quartz.Scheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
        JobTriggers jobTriggers = new JobTriggers(sched);
        sched.scheduleJob(jobTriggers.defineJob(), jobTriggers.defineTrigger());
    }

}
