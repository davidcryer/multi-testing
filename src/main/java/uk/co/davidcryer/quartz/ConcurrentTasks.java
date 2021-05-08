package uk.co.davidcryer.quartz;

import org.quartz.*;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static uk.co.davidcryer.quartz.AbstractTaskJob.PROPS_JOB_NEXT_GROUP;
import static uk.co.davidcryer.quartz.AbstractTaskJob.PROPS_JOB_NEXT_NAME;

public class ConcurrentTasks extends Task {
    private final Class<? extends Job> concurrentJobClass;

    public ConcurrentTasks(String key,
                          Function<JobDataMap, JobDataMap> propsMapper,
                          Class<? extends Job> concurrentJobClass) {
        this(key, propsMapper, IMPLIED_SUCCESS_PREDICATE, concurrentJobClass);
    }

    public ConcurrentTasks(String key,
                          Function<JobDataMap, JobDataMap> propsMapper,
                          Predicate<JobDataMap> successfulJobCondition,
                          Class<? extends Job> concurrentJobClass) {
        this(key, propsMapper, successfulJobCondition, NO_OP_CONSUMER, concurrentJobClass);
    }

    public ConcurrentTasks(String key,
                          Function<JobDataMap, JobDataMap> propsMapper,
                          Predicate<JobDataMap> successfulJobCondition,
                          Consumer<JobDataMap> returnPropsWriter,
                          Class<? extends Job> concurrentJobClass) {
        super(key, propsMapper, successfulJobCondition, returnPropsWriter);
        this.concurrentJobClass = concurrentJobClass;
    }

    @Override
    protected void triggerJob(JobExecutionContext context, JobDataMap props, Scheduler scheduler) throws SchedulerException {
        var jobProps = propsMapper.apply(props);
        var thisJobKey = context.getJobDetail().getKey();
        jobProps.put(PROPS_JOB_NEXT_NAME, thisJobKey.getName());
        if (thisJobKey.getGroup() != null) {
            jobProps.put(PROPS_JOB_NEXT_GROUP, thisJobKey.getGroup());
        }
        var jobKey = JobKey.jobKey(key, UUID.randomUUID().toString());
        scheduler.addJob(JobBuilder.newJob(concurrentJobClass).withIdentity(jobKey).storeDurably().usingJobData(jobProps).build(), false);
        scheduler.triggerJob(jobKey);
    }
}
