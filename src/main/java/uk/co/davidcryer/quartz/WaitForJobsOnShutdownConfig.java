package uk.co.davidcryer.quartz;

import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WaitForJobsOnShutdownConfig {

    @Bean
    public WaitForJobsOnShutdownJobListener waitForJobsOnShutdownJobListener() {
        return new WaitForJobsOnShutdownJobListener();
    }

    @Bean
    public WaitForJobsOnShutdownApplicationListener waitForJobsOnShutdownApplicationListener(Scheduler scheduler,
                                                                                             WaitForJobsOnShutdownJobListener waitForJobsOnShutdownJobListener) {
        return new WaitForJobsOnShutdownApplicationListener(scheduler, waitForJobsOnShutdownJobListener);
    }
}
