package uk.co.davidcryer.jobs;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.List;
import java.util.UUID;
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
            log.info("{} executing concurrent tasks job with last job {}", key, lastJob);
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
                .orElseThrow(() -> new JobExecutionException(key + " does not have task for last job " + lastJob));
        jobProps.put(lastJob, task.successfulJobCondition.test(props));
        //TODO allow for last job return data to be set to jobProps
        // task.setReturnProps(jobProps)
        if (areAllTasksComplete(context, tasks)) {
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

    @Getter
    public static class Task {
        private final String key;
        private final Function<JobDataMap, JobDataMap> propsMapper;
        private final Predicate<JobDataMap> successfulJobCondition;

        public Task(String key, Function<JobDataMap, JobDataMap> propsMapper) {
            this(key, propsMapper, ignore -> true);
        }

        public Task(String key, Function<JobDataMap, JobDataMap> propsMapper, Predicate<JobDataMap> successfulJobCondition) {
            this.key = key;
            this.propsMapper = propsMapper;
            this.successfulJobCondition = successfulJobCondition;
        }

        protected void triggerJob(JobExecutionContext context, JobDataMap props, Scheduler scheduler) throws SchedulerException {
            var triggerProps = propsMapper.apply(props);
            triggerProps.put(PROPS_JOB_NEXT, context.getJobDetail().getKey().getName());
            scheduler.triggerJob(JobKey.jobKey(key), triggerProps);
        }
    }

    @Getter
    public static class ConcurrentTask extends Task {
        private final Class<? extends Job> concurrentJobClass;

        public ConcurrentTask(String key,
                              Function<JobDataMap, JobDataMap> propsMapper,
                              Class<? extends Job> concurrentJobClass) {
            super(key, propsMapper);
            this.concurrentJobClass = concurrentJobClass;
        }

        public ConcurrentTask(String key,
                    Function<JobDataMap, JobDataMap> propsMapper,
                    Predicate<JobDataMap> successfulJobCondition,
                    Class<? extends Job> concurrentJobClass) {
            super(key, propsMapper, successfulJobCondition);
            this.concurrentJobClass = concurrentJobClass;
        }

        @Override
        protected void triggerJob(JobExecutionContext context, JobDataMap props, Scheduler scheduler) throws SchedulerException {
            var jobProps = getPropsMapper().apply(props);
            jobProps.put(PROPS_JOB_NEXT, context.getJobDetail().getKey().getName());
            var name = getKey() + "-" + UUID.randomUUID();
            scheduler.addJob(JobBuilder.newJob(concurrentJobClass).withIdentity(name).storeDurably().usingJobData(jobProps).build(), false);
            scheduler.triggerJob(JobKey.jobKey(name));
        }
    }
}
