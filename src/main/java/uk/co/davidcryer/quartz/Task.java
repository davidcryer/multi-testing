package uk.co.davidcryer.quartz;

import org.quartz.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static uk.co.davidcryer.quartz.AbstractTaskJob.PROPS_JOB_NEXT_GROUP;
import static uk.co.davidcryer.quartz.AbstractTaskJob.PROPS_JOB_NEXT_NAME;

public class Task {
    static final Predicate<JobDataMap> IMPLIED_SUCCESS_PREDICATE = ignore -> true;
    static final Consumer<JobDataMap> NO_OP_CONSUMER = props -> {};
    final String key;
    final Function<JobDataMap, JobDataMap> propsMapper;
    final Predicate<JobDataMap> successfulJobCondition;
    final Consumer<JobDataMap> returnPropsWriter;

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

    public String getKey() {
        return key;
    }

    public Predicate<JobDataMap> getSuccessfulJobCondition() {
        return successfulJobCondition;
    }

    public Consumer<JobDataMap> getReturnPropsWriter() {
        return returnPropsWriter;
    }
}
