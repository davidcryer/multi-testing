package uk.co.davidcryer.quartz;

import org.quartz.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConcurrentTasks extends Task {
    private final Class<? extends Job> concurrentJobClass;

    public ConcurrentTasks(String key,
                          Function<JobDataMap, JobDataMap> propsMapper,
                          Class<? extends Job> concurrentJobClass) {
        super(key, propsMapper);
        this.concurrentJobClass = concurrentJobClass;
    }

    public ConcurrentTasks(String key,
                          Function<JobDataMap, JobDataMap> propsMapper,
                          Predicate<JobDataMap> successfulJobCondition,
                          Class<? extends Job> concurrentJobClass) {
        super(key, propsMapper, successfulJobCondition);
        this.concurrentJobClass = concurrentJobClass;
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
    public void triggerJob(JobExecutionContext context, JobDataMap props, Scheduler scheduler) throws SchedulerException {
        TaskUtils.triggerConcurrentJob(context, props, scheduler, getKey(), getPropsMapper(), concurrentJobClass);
    }
}
