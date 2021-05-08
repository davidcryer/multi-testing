package uk.co.davidcryer.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;

import static uk.co.davidcryer.quartz.JobExecutionContextUtils.getJobName;

@RequiredArgsConstructor
@Slf4j
public abstract class TaskJob implements Job, JobReturn {
    private final Scheduler scheduler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Executing {} task job", getJobName(context));
        executeTask(context);
        triggerReturnJob(context, scheduler);
    }

    protected abstract void executeTask(JobExecutionContext context) throws JobExecutionException;
}
