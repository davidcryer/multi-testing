package uk.co.davidcryer.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.List;
import java.util.function.Predicate;

import static uk.co.davidcryer.quartz.JobExecutionContextUtils.getJobName;
import static uk.co.davidcryer.quartz.JobUtils.getLastJobKey;
import static uk.co.davidcryer.quartz.JobUtils.triggerReturnJob;
import static uk.co.davidcryer.quartz.ReturnPropsWriter.getErrorWriterForReturnProps;
import static uk.co.davidcryer.quartz.TaskUtils.*;

@RequiredArgsConstructor
@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public abstract class TaskBatchJob implements Job, ReturnPropsWriter {
    private final Scheduler scheduler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            var lastJob = getLastJobKey(context);
            if (lastJob == null) {
                log.info("Executing {} task batch job for first time", getJobName(context));
                triggerJobs(context);
            } else {
                log.info("Executing {} task batch job with last job {}", getJobName(context), lastJob);
                handleLastJob(context, lastJob);
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    private void triggerJobs(JobExecutionContext context) throws SchedulerException {
        for (Task task : getTasks(context)) {
            task.triggerJob(context, scheduler);
        }
    }

    private void handleLastJob(JobExecutionContext context, String lastTaskKey) throws SchedulerException {
        var jobProps = context.getJobDetail().getJobDataMap();
        var tasks = getTasks(context);
        var lastTask = tasks.stream()
                .filter(t -> t.getKey().equals(lastTaskKey))
                .findFirst()
                .orElseThrow(() -> new JobExecutionException(getJobName(context) + " does not have task for last job " + lastTaskKey));
        if (isLastJobErrored(context)) {
            if (!lastTask.getAllowedToError()) {
                var error = getLastJobError(context);
                addErroredTaskEntry(jobProps, lastTask, error);
                lastTask.getErrorRecovery().run();
            }
        } else {
            lastTask.getOnReturnListener().run();
        }
        jobProps.put(lastTask.getKey(), true);
        if (areAllTasksComplete(jobProps, tasks)) {
            if (hasErroredTasks(jobProps)) {
                var erroredTasks = getErroredTaskEntries(jobProps);
                var error = String.join("\n", erroredTasks);
                log.info("{} marked as errored with message: {}", getJobName(context), error);
                markAsErrored(context, error);
                triggerReturnJob(context, scheduler, getErrorWriterForReturnProps(error));
                return;
            }
            markAsFinished(context);
            triggerReturnJob(context, scheduler, this::writeToReturnProps);
        }
    }

    private boolean areAllTasksComplete(JobDataMap props, List<Task> tasks) {
        Predicate<String> predicate = key -> props.containsKey(key) && props.getBoolean(key);
        return tasks.stream()
                .map(Task::getKey)
                .allMatch(predicate);
    }

    protected abstract List<Task> getTasks(JobExecutionContext context);
}
