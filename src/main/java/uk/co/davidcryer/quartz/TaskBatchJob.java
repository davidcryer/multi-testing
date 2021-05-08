package uk.co.davidcryer.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.List;
import java.util.function.Predicate;

import static uk.co.davidcryer.quartz.JobExecutionContextUtils.getJobName;
import static uk.co.davidcryer.quartz.TaskUtils.getLastJobKey;
import static uk.co.davidcryer.quartz.TaskUtils.hasLastJobKey;

@RequiredArgsConstructor
@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public abstract class TaskBatchJob implements Job, JobReturn, MarkableAsFinished {
    private final Scheduler scheduler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var props = context.getMergedJobDataMap();
        try {
            var lastJob = hasLastJobKey(props) ? getLastJobKey(props) : "";
            log.info("{} executing task batch job with last job {}", getJobName(context), lastJob);
            if (lastJob.equals("")) {
                triggerJobs(context, props);
            } else {
                handleLastJob(context, props, lastJob);
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    private void triggerJobs(JobExecutionContext context, JobDataMap props) throws SchedulerException {
        for (Task task : getTasks()) {
            task.triggerJob(context, props, scheduler);
        }
    }

    private void handleLastJob(JobExecutionContext context, JobDataMap props, String lastJob) throws SchedulerException {
        var jobProps = context.getJobDetail().getJobDataMap();
        var tasks = getTasks();
        var task = tasks.stream()
                .filter(t -> t.getKey().equals(lastJob))
                .findFirst()
                .orElseThrow(() -> new JobExecutionException(getJobName(context) + " does not have task for last job " + lastJob));
        jobProps.put(lastJob, task.getSuccessfulJobCondition().test(props));
        task.getReturnPropsWriter().accept(jobProps);
        if (areAllTasksComplete(context, tasks)) {
            markAsFinished(context, jobProps);
            triggerReturnJob(context, scheduler);
        }
    }

    private boolean areAllTasksComplete(JobExecutionContext context, List<Task> tasks) {
        var props = context.getJobDetail().getJobDataMap();
        Predicate<String> predicate = key -> props.containsKey(key) && props.getBoolean(key);
        return tasks.stream()
                .map(Task::getKey)
                .allMatch(predicate);
    }

    protected abstract List<Task> getTasks();
}
