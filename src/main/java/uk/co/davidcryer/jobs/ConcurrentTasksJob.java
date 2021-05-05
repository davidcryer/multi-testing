package uk.co.davidcryer.jobs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static uk.co.davidcryer.jobs.TaskJob.PROPS_JOB_LAST;
import static uk.co.davidcryer.jobs.TaskJob.PROPS_JOB_NEXT;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public abstract class ConcurrentTasksJob implements Job {
    private final Scheduler scheduler;
    private final String key;
    private final List<ConcurrentTask> concurrentTasks;

    public ConcurrentTasksJob(Scheduler scheduler, String key, String... concurrentTaskKeys) {
        this.scheduler = scheduler;
        this.key = key;
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

    //from task
    private void triggerNextJob(JobExecutionContext context) throws JobExecutionException {
        try {
            var props = context.getMergedJobDataMap();
            if (props.containsKey(PROPS_JOB_NEXT)) {
                var nextJob = props.getString(PROPS_JOB_NEXT);
                var nextProps = new JobDataMap();
                nextProps.put(PROPS_JOB_LAST, key);
                writeToReturnProps(context, nextProps);
                scheduler.triggerJob(JobKey.jobKey(nextJob), nextProps);
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    private boolean areAllConcurrentTasksComplete(JobExecutionContext context) {
        var props = context.getJobDetail().getJobDataMap();
        Predicate<String> predicate = key -> props.containsKey(key) && props.getBoolean(key);
        return concurrentTasks.stream()
                .map(ConcurrentTask::getKey)
                .allMatch(predicate);
    }

    protected void writeToReturnProps(JobExecutionContext context, JobDataMap props) {

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
