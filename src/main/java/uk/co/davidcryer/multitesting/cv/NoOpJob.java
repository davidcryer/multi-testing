package uk.co.davidcryer.multitesting.cv;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.TaskJob;

@Component
@Slf4j
public class NoOpJob extends TaskJob {
    public static final String KEY = "no-op";
    private static int instanceCount = 0;
    private final int instance = instanceCount++;

    @Autowired
    public NoOpJob(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected void executeTask(JobExecutionContext context) throws JobExecutionException {
        log.info("instance {}", instance);
    }
}
