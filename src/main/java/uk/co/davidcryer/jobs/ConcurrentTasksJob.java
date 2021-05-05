package uk.co.davidcryer.jobs;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public abstract class ConcurrentTasksJob extends AbstractTaskJob {
    private final Map<String, ConcurrentTask> concurrentTaskMap;

    public ConcurrentTasksJob(Scheduler scheduler, String key, List<ConcurrentTask> concurrentTasks) {
        super(scheduler, key);
        this.concurrentTaskMap = concurrentTasks.stream()
                .collect(Collectors.toMap(ConcurrentTask::getKey, Function.identity()));
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
        for (ConcurrentTask task : concurrentTaskMap.values()) {
            triggerJob(context, task.getKey(), task.propsMapper.apply(props));
        }
    }

    private void handleLastJob(JobExecutionContext context, JobDataMap props, String lastJob) throws SchedulerException {
        var jobProps = context.getJobDetail().getJobDataMap();
        var concurrentTask = concurrentTaskMap.get(lastJob);
        if (concurrentTask == null) {
            throw new JobExecutionException("ConcurrentTask does not exist for last job " + lastJob);
        }
        jobProps.put(lastJob, concurrentTask.successfulJobCondition.test(props));
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
        return concurrentTaskMap.values()
                .stream()
                .map(ConcurrentTask::getKey)
                .allMatch(predicate);
    }

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
