package uk.co.davidcryer.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;

@Slf4j
public abstract class TaskJob extends AbstractTaskJob {

    public TaskJob(Scheduler scheduler, String key) {
        super(scheduler, key);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("executing job {}", key);
        executeTask(context);
        triggerNextJob(context);
    }

    protected abstract void executeTask(JobExecutionContext context) throws JobExecutionException;
}
