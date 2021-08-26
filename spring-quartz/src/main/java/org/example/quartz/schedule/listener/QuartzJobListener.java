package org.example.quartz.schedule.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.core.QuartzScheduler;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.stereotype.Component;

/**
 * @author renc
 */
@Component
public class QuartzJobListener extends JobListenerSupport {

    @Override
    public String getName() {
        return "QuartzJobListener";
    }

    /**
     * @see QuartzScheduler#notifyJobListenersToBeExecuted(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        // do something with the event
        System.out.println(">>>>>>>> jobToBeExecuted");
    }

    /**
     * @see QuartzScheduler#notifyJobListenersWasVetoed(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        // do something with the event
        System.out.println(">>>>>>>> jobExecutionVetoed");
    }

    /**
     * @see org.quartz.core.QuartzScheduler#notifyJobListenersWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        // do something with the event
        System.out.println(">>>>>>>> jobWasExecuted");
    }
}
