package uk.co.davidcryer.jobs;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public abstract class ConcurrentTasksJob extends AbstractTaskJob {

    public ConcurrentTasksJob(Scheduler scheduler, String key) {
        super(scheduler, key);
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
                handleLastJob(context, props, lastJob);
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    protected void triggerConcurrentTasks(JobExecutionContext context, JobDataMap props) throws SchedulerException {
        for (ConcurrentTask task : getConcurrentTasks()) {
            triggerJob(context, task.getKey(), task.propsMapper.apply(props));
        }
    }

    private void handleLastJob(JobExecutionContext context, JobDataMap props, String lastJob) throws SchedulerException {
        var jobProps = context.getJobDetail().getJobDataMap();
        var concurrentTasks = getConcurrentTasks();
        var concurrentTask = concurrentTasks.stream()
                .filter(task -> task.getKey().equals(lastJob))
                .findFirst()
                .orElseThrow(() -> new JobExecutionException("Concurrent task does not exist for last job " + lastJob));
        jobProps.put(lastJob, concurrentTask.successfulJobCondition.test(props));
        if (areAllConcurrentTasksComplete(context, concurrentTasks)) {
            triggerNextJob(context);
        }
    }

    protected void triggerJob(JobExecutionContext context, String name, JobDataMap props) throws SchedulerException {
        props.put(PROPS_JOB_NEXT, context.getJobDetail().getKey().getName());
        scheduler.triggerJob(JobKey.jobKey(name), props);
    }

    private boolean areAllConcurrentTasksComplete(JobExecutionContext context, List<ConcurrentTask> concurrentTasks) {
        var props = context.getJobDetail().getJobDataMap();
        Predicate<String> predicate = key -> props.containsKey(key) && props.getBoolean(key);
        return concurrentTasks.stream()
                .map(ConcurrentTask::getKey)
                .allMatch(predicate);
    }

    protected abstract List<ConcurrentTask> getConcurrentTasks();

    @Getter
    public static class ConcurrentTask {
        private final String key;
        private final Function<JobDataMap, JobDataMap> propsMapper;
        private final Predicate<JobDataMap> successfulJobCondition;

        public ConcurrentTask(String key, Function<JobDataMap, JobDataMap> propsMapper) {
            this.key = key;
            this.propsMapper = propsMapper;
            successfulJobCondition = ignore -> true;
        }

        public ConcurrentTask(String key, Function<JobDataMap, JobDataMap> propsMapper, Predicate<JobDataMap> successfulJobCondition) {
            this.key = key;
            this.propsMapper = propsMapper;
            this.successfulJobCondition = successfulJobCondition;
        }
    }
}
