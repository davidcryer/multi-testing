package uk.co.davidcryer.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static uk.co.davidcryer.quartz.Task.getLastJobKey;
import static uk.co.davidcryer.quartz.Task.hasLastJobKey;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public abstract class OrchestratorJob implements Job, MarkableAsFinished {
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
        if (hasLastJobKey(props)) {
            var lastJobKey = getLastJobKey(props);
            log.info("executing orchestrator with last job {}", lastJobKey);
            Task lastTask = null;
            for (int i = 0; i < tasks.size(); i++) {
                var task = tasks.get(i);
                if (task.key.equals(lastJobKey)) {
                    lastTask = task;
                    if (i < tasks.size() - 1) {
                        nextTask = tasks.get(i + 1);
                    }
                }
            }
            if (lastTask == null) {
                throw new JobExecutionException("Task does not exist for last job key " + lastJobKey);
            }
            if (nextTask == null) {
                markAsFinished(context, props);
                return;
            }
        } else {
            log.info("executing orchestrator for first job");
            nextTask = getTasks().get(0);
        }
        nextTask.triggerJob(context, props, scheduler);
    }

    protected abstract List<Task> getTasks();
}
