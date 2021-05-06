package uk.co.davidcryer.multitesting.config;

import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.WaitForJobsOnShutdownJobListener;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WaitForJobsOnShutdownApplicationListener implements ApplicationListener<ContextClosedEvent> {
    private final Scheduler scheduler;
    private final WaitForJobsOnShutdownJobListener jobListener;

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
