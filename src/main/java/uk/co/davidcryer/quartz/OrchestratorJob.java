package uk.co.davidcryer.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static uk.co.davidcryer.quartz.JobExecutionContextUtils.getJobName;
import static uk.co.davidcryer.quartz.JobUtils.getLastJobKey;
import static uk.co.davidcryer.quartz.TaskUtils.*;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public abstract class OrchestratorJob implements Job {
    private final Scheduler scheduler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var props = context.getMergedJobDataMap();
        try {
            handle(context, props);
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    private void handle(JobExecutionContext context, JobDataMap props) throws SchedulerException {
        Task nextTask = null;
        var tasks = getTasks();
        var lastJobKey = getLastJobKey(props);
        if (lastJobKey == null) {
            log.info("Executing {} orchestrator for first job", getJobName(context));
            nextTask = getTasks().get(0);
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
            if (isErrored(props)) {
                if (!lastTask.getAllowedToError()) {
                    var error = getError(props);
                    markAsErrored(context.getJobDetail().getJobDataMap(), error);
                    markAsFinished(context.getJobDetail().getJobDataMap());
                    return;
                }
            } else {
                lastTask.getReturnPropsConsumer().accept(props, context.getJobDetail().getJobDataMap());
            }
            if (nextTask == null) {
                markAsFinished(context.getJobDetail().getJobDataMap());
                return;
            }
        }
        nextTask.triggerJob(context, props, scheduler);
    }

    protected abstract List<Task> getTasks();
}
