package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.TaskJob;

@Component
public class NoOpJob extends TaskJob {
    public static final String KEY = "no-op";

    @Autowired
    public NoOpJob(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected void executeTask(JobExecutionContext context) throws JobExecutionException {

    }
}
