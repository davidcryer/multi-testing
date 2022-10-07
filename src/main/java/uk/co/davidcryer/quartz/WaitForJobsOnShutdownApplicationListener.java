package uk.co.davidcryer.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class WaitForJobsOnShutdownApplicationListener implements ApplicationListener<ContextClosedEvent> {
    private final Scheduler scheduler;
    private final WaitForJobsOnShutdownJobListener jobListener;

    public WaitForJobsOnShutdownApplicationListener(Scheduler scheduler, WaitForJobsOnShutdownJobListener jobListener) {
        this.scheduler = scheduler;
        this.jobListener = jobListener;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        try {
            scheduler.standby();
            jobListener.waitOnShutdownLock();
        } catch (InterruptedException | SchedulerException e) {
            e.printStackTrace();
        }
    }
}
