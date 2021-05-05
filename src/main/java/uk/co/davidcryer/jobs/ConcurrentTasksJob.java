package uk.co.davidcryer.jobs;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public abstract class ConcurrentTasksJob extends AbstractTaskJob {
    private final List<ConcurrentTask> concurrentTasks;

    public ConcurrentTasksJob(Scheduler scheduler, String key, String... concurrentTaskKeys) {
        super(scheduler, key);
        this.concurrentTasks = Arrays.stream(concurrentTaskKeys)
                .map(ConcurrentTask::new)
                .collect(Collectors.toList());
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var props = context.getMergedJobDataMap();
        try {
            var lastJob = props.containsKey(PROPS_JOB_LAST) ? props.getString(PROPS_JOB_LAST) : "";
            log.info("executing concurrent tasks job with last job {}", lastJob);
            if (lastJob.equals("")) {
                triggerConcurrentTasks(context, props);
            } else {
                handleLastJob(context, lastJob);
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    protected abstract void triggerConcurrentTasks(JobExecutionContext context, JobDataMap props) throws SchedulerException;

    private void handleLastJob(JobExecutionContext context, String lastJob) throws SchedulerException {
        var jobProps = context.getJobDetail().getJobDataMap();
        jobProps.put(lastJob, true);
        if (areAllConcurrentTasksComplete(context)) {
            triggerNextJob(context);
        }
    }

    protected void triggerJob(JobExecutionContext context, String name, JobDataMap props) throws SchedulerException {
        props.put(PROPS_JOB_NEXT, context.getJobDetail().getKey().getName());
        scheduler.triggerJob(JobKey.jobKey(name), props);
    }

    private boolean areAllConcurrentTasksComplete(JobExecutionContext context) {
        var props = context.getJobDetail().getJobDataMap();
        Predicate<String> predicate = key -> props.containsKey(key) && props.getBoolean(key);
        return concurrentTasks.stream()
                .map(ConcurrentTask::getKey)
                .allMatch(predicate);
    }

    @Getter
    public static class ConcurrentTask {
        private final String key;
        private final Predicate<JobDataMap> successfulJobCondition;

        public ConcurrentTask(String key) {
            this.key = key;
            successfulJobCondition = ignore -> true;
        }

        public ConcurrentTask(String key, Predicate<JobDataMap> successfulJobCondition) {
            this.key = key;
            this.successfulJobCondition = successfulJobCondition;
        }
    }
}
