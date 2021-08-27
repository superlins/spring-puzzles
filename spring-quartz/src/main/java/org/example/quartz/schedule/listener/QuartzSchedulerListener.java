package org.example.quartz.schedule.listener;

import org.quartz.*;
import org.quartz.listeners.SchedulerListenerSupport;
import org.springframework.stereotype.Component;

/**
 * @author renc
 */
@Component
public class QuartzSchedulerListener extends SchedulerListenerSupport {

    @Override
    public void jobAdded(JobDetail jobDetail) {
        super.jobAdded(jobDetail);
    }

    @Override
    public void jobDeleted(JobKey jobKey) {
        super.jobDeleted(jobKey);
    }

    @Override
    public void jobPaused(JobKey jobKey) {
        super.jobPaused(jobKey);
    }

    @Override
    public void jobResumed(JobKey jobKey) {
        super.jobResumed(jobKey);
    }

    @Override
    public void jobScheduled(Trigger trigger) {
        super.jobScheduled(trigger);
    }

    @Override
    public void jobsPaused(String jobGroup) {
        super.jobsPaused(jobGroup);
    }

    @Override
    public void jobsResumed(String jobGroup) {
        super.jobsResumed(jobGroup);
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
        super.jobUnscheduled(triggerKey);
    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        super.schedulerError(msg, cause);
    }

    @Override
    public void schedulerInStandbyMode() {
        super.schedulerInStandbyMode();
    }

    @Override
    public void schedulerShutdown() {
        super.schedulerShutdown();
    }

    @Override
    public void schedulerShuttingdown() {
        super.schedulerShuttingdown();
    }

    @Override
    public void schedulerStarted() {
        super.schedulerStarted();
    }

    @Override
    public void schedulerStarting() {
        super.schedulerStarting();
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        super.triggerFinalized(trigger);
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {
        super.triggerPaused(triggerKey);
    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {
        super.triggerResumed(triggerKey);
    }

    @Override
    public void triggersPaused(String triggerGroup) {
        super.triggersPaused(triggerGroup);
    }

    @Override
    public void triggersResumed(String triggerGroup) {
        super.triggersResumed(triggerGroup);
    }

    @Override
    public void schedulingDataCleared() {
        super.schedulingDataCleared();
    }
}
