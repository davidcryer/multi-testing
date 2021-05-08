package uk.co.davidcryer.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.List;
import java.util.function.Predicate;

import static uk.co.davidcryer.quartz.JobExecutionContextUtils.getJobName;

@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public abstract class ConcurrentTasksJob extends AbstractTaskJob implements MarkableAsFinished {

    public ConcurrentTasksJob(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var props = context.getMergedJobDataMap();
        try {
            var lastJob = props.containsKey(PROPS_JOB_LAST) ? props.getString(PROPS_JOB_LAST) : "";
            log.info("{} executing concurrent tasks job with last job {}", getJobName(context), lastJob);
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
            triggerNextJob(context);
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
