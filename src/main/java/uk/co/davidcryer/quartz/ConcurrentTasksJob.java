package uk.co.davidcryer.quartz;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

    @Getter
    public static class Task {
        static final Predicate<JobDataMap> IMPLIED_SUCCESS_PREDICATE = ignore -> true;
        static final Consumer<JobDataMap> NO_OP_CONSUMER = props -> {};
        private final String key;
        private final Function<JobDataMap, JobDataMap> propsMapper;
        private final Predicate<JobDataMap> successfulJobCondition;
        private final Consumer<JobDataMap> returnPropsWriter;

        public Task(String key, Function<JobDataMap, JobDataMap> propsMapper) {
            this(key, propsMapper, IMPLIED_SUCCESS_PREDICATE, NO_OP_CONSUMER);
        }

        public Task(String key,
                    Function<JobDataMap, JobDataMap> propsMapper,
                    Predicate<JobDataMap> successfulJobCondition,
                    Consumer<JobDataMap> returnPropsWriter) {
            this.key = key;
            this.propsMapper = propsMapper;
            this.successfulJobCondition = successfulJobCondition;
            this.returnPropsWriter = returnPropsWriter;
        }

        protected void triggerJob(JobExecutionContext context, JobDataMap props, Scheduler scheduler) throws SchedulerException {
            var triggerProps = propsMapper.apply(props);
            var thisJobKey = context.getJobDetail().getKey();
            triggerProps.put(PROPS_JOB_NEXT_NAME, thisJobKey.getName());
            if (thisJobKey.getGroup() != null) {
                triggerProps.put(PROPS_JOB_NEXT_GROUP, thisJobKey.getGroup());
            }
            scheduler.triggerJob(JobKey.jobKey(key), triggerProps);
        }
    }

    public static class ConcurrentTask extends Task {
        private final Class<? extends Job> concurrentJobClass;

        public ConcurrentTask(String key,
                              Function<JobDataMap, JobDataMap> propsMapper,
                              Class<? extends Job> concurrentJobClass) {
            this(key, propsMapper, IMPLIED_SUCCESS_PREDICATE, concurrentJobClass);
        }

        public ConcurrentTask(String key,
                    Function<JobDataMap, JobDataMap> propsMapper,
                    Predicate<JobDataMap> successfulJobCondition,
                    Class<? extends Job> concurrentJobClass) {
            this(key, propsMapper, successfulJobCondition, NO_OP_CONSUMER, concurrentJobClass);
        }

        public ConcurrentTask(String key,
                              Function<JobDataMap, JobDataMap> propsMapper,
                              Predicate<JobDataMap> successfulJobCondition,
                              Consumer<JobDataMap> returnPropsWriter,
                              Class<? extends Job> concurrentJobClass) {
            super(key, propsMapper, successfulJobCondition, returnPropsWriter);
            this.concurrentJobClass = concurrentJobClass;
        }

        @Override
        protected void triggerJob(JobExecutionContext context, JobDataMap props, Scheduler scheduler) throws SchedulerException {
            var jobProps = getPropsMapper().apply(props);
            var thisJobKey = context.getJobDetail().getKey();
            jobProps.put(PROPS_JOB_NEXT_NAME, thisJobKey.getName());
            if (thisJobKey.getGroup() != null) {
                jobProps.put(PROPS_JOB_NEXT_GROUP, thisJobKey.getGroup());
            }
            var name = getKey();
            var jobKey = JobKey.jobKey(name, UUID.randomUUID().toString());
            scheduler.addJob(JobBuilder.newJob(concurrentJobClass).withIdentity(jobKey).storeDurably().usingJobData(jobProps).build(), false);
            scheduler.triggerJob(jobKey);
        }
    }
}
