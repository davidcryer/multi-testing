package uk.co.davidcryer.quartz;

import org.quartz.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Task {
    static final String PROPS_JOB_LAST = "job.last";
    static final String PROPS_JOB_RETURN_NAME = "job.return.name";
    static final String PROPS_JOB_RETURN_GROUP = "job.return.group";
    static final Predicate<JobDataMap> IMPLIED_SUCCESS_PREDICATE = ignore -> true;
    static final Consumer<JobDataMap> NO_OP_CONSUMER = props -> {};
    final String key;
    final Function<JobDataMap, JobDataMap> propsMapper;
    final Predicate<JobDataMap> successfulJobCondition;
    final Consumer<JobDataMap> returnPropsWriter;

    public Task(String key, Function<JobDataMap, JobDataMap> propsMapper) {
        this(key, propsMapper, IMPLIED_SUCCESS_PREDICATE, NO_OP_CONSUMER);
    }

    public Task(String key, Function<JobDataMap, JobDataMap> propsMapper, Predicate<JobDataMap> successfulJobCondition) {
        this(key, propsMapper, successfulJobCondition, NO_OP_CONSUMER);
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
        triggerProps.put(PROPS_JOB_RETURN_NAME, thisJobKey.getName());
        if (thisJobKey.getGroup() != null) {
            triggerProps.put(PROPS_JOB_RETURN_GROUP, thisJobKey.getGroup());
        }
        scheduler.triggerJob(JobKey.jobKey(key), triggerProps);
    }

    static void triggerReturnJob(JobExecutionContext context,
                                 Scheduler scheduler,
                                 BiConsumer<JobExecutionContext, JobDataMap> returnPropsWriter) throws SchedulerException {
        var props = context.getMergedJobDataMap();
        if (props.containsKey(PROPS_JOB_RETURN_NAME)) {
            var returnProps = new JobDataMap();
            returnProps.put(PROPS_JOB_LAST, context.getJobDetail().getKey().getName());
            returnPropsWriter.accept(context, returnProps);
            var returnJobKey = getReturnJobKey(props);
            scheduler.triggerJob(returnJobKey, returnProps);
        }
    }

    private static JobKey getReturnJobKey(JobDataMap props) {
        var name = props.getString(PROPS_JOB_RETURN_NAME);
        if (props.containsKey(PROPS_JOB_RETURN_GROUP)) {
            var group = props.getString(PROPS_JOB_RETURN_GROUP);
            return JobKey.jobKey(name, group);
        }
        return JobKey.jobKey(name);
    }

    static boolean hasLastJobKey(JobDataMap props) {
        return props.containsKey(PROPS_JOB_LAST);
    }

    static String getLastJobKey(JobDataMap props) {
        return props.getString(PROPS_JOB_LAST);
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
