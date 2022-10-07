package uk.co.davidcryer.quartz;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.co.davidcryer.quartz.JobExecutionContextUtils.getJobName;
import static uk.co.davidcryer.quartz.JobUtils.triggerReturnJob;
import static uk.co.davidcryer.quartz.ReturnPropsWriter.getErrorWriterForReturnProps;

public abstract class TaskJob implements Job, ReturnPropsWriter {
    private static final Logger log = LoggerFactory.getLogger(TaskJob.class);
    private final Scheduler scheduler;

    public TaskJob(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Executing {} task job", getJobName(context));
        try {
            try {
                executeTask(context);
                triggerReturnJob(context, scheduler, this::writeToReturnProps);
            } catch (Throwable t) {
                var error = t.getMessage();
                log.info("{} errored with message: {}", getJobName(context), error);
                triggerReturnJob(context, scheduler, getErrorWriterForReturnProps(error));
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    protected abstract void executeTask(JobExecutionContext context) throws JobExecutionException;
}
