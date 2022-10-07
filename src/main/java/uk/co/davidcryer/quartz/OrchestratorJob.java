package uk.co.davidcryer.quartz;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static uk.co.davidcryer.quartz.JobExecutionContextUtils.getJobName;
import static uk.co.davidcryer.quartz.JobUtils.getLastJobKey;
import static uk.co.davidcryer.quartz.TaskUtils.*;

public abstract class OrchestratorJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(OrchestratorJob.class);
    private final Scheduler scheduler;

    public OrchestratorJob(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            handle(context);
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    private void handle(JobExecutionContext context) throws SchedulerException {
        Task nextTask = null;
        var tasks = getTasks(context);
        var lastJobKey = getLastJobKey(context);
        if (lastJobKey == null) {
            log.info("Executing {} orchestrator for first job", getJobName(context));
            nextTask = tasks.get(0);
        } else {
            log.info("Executing {} orchestrator with last job {}", getJobName(context), lastJobKey);
            Task lastTask = null;
            for (int i = 0; i < tasks.size(); i++) {
                var task = tasks.get(i);
                if (task.getKey().equals(lastJobKey)) {
                    lastTask = task;
                    if (i < tasks.size() - 1) {
                        nextTask = tasks.get(i + 1);
                    }
                    break;
                }
            }
            if (lastTask == null) {
                throw new JobExecutionException("Task does not exist for last job key " + lastJobKey);
            }
            if (isLastJobErrored(context)) {
                if (!lastTask.getAllowedToError()) {
                    var error = getLastJobError(context);
                    log.info("{} marked as errored with message: {}", getJobName(context), error);
                    lastTask.getErrorRecovery().run();
                    markAsErrored(context, error);
                    return;
                }
            } else {
                lastTask.getOnReturnListener().run();
            }
            if (nextTask == null) {
                markAsFinished(context);
                return;
            }
        }
        nextTask.triggerJob(context, scheduler);
    }

    protected abstract List<Task> getTasks(JobExecutionContext context);
}
