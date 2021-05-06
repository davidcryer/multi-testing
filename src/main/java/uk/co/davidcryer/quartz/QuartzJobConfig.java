package uk.co.davidcryer.quartz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzJobConfig {

    @Bean
    public WaitForJobsOnShutdownJobListener waitForJobsOnShutdownJobListener() {
        return new WaitForJobsOnShutdownJobListener();
    }
}
