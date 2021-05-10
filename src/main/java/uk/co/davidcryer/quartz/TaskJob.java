package uk.co.davidcryer.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import static uk.co.davidcryer.quartz.JobExecutionContextUtils.getJobName;
import static uk.co.davidcryer.quartz.JobUtils.triggerReturnJob;
import static uk.co.davidcryer.quartz.ReturnPropsWriter.getErrorWriterForReturnProps;

@RequiredArgsConstructor
@Slf4j
public abstract class TaskJob implements Job, ReturnPropsWriter {
    private final Scheduler scheduler;

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
