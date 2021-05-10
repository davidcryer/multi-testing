package uk.co.davidcryer.quartz;

import org.quartz.*;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class JobUtils {
    private static final String PROPS_JOB_LAST = "job.last";
    private static final String PROPS_JOB_RETURN_NAME = "job.return.name";
    private static final String PROPS_JOB_RETURN_GROUP = "job.return.group";

    public static void triggerJob(JobExecutionContext context,
                                  JobDataMap props,
                                  Scheduler scheduler,
                                  String key,
                                  Function<JobDataMap, JobDataMap> propsMapper) throws SchedulerException {
        var triggerProps = propsMapper.apply(props);
        var thisJobKey = context.getJobDetail().getKey();
        triggerProps.put(PROPS_JOB_RETURN_NAME, thisJobKey.getName());
        if (thisJobKey.getGroup() != null) {
            triggerProps.put(PROPS_JOB_RETURN_GROUP, thisJobKey.getGroup());
        }
        scheduler.triggerJob(JobKey.jobKey(key), triggerProps);
    }

    public static void triggerBatchJob(JobExecutionContext context,
                                       JobDataMap props,
                                       Scheduler scheduler,
                                       String key,
                                       Function<JobDataMap, JobDataMap> propsMapper,
                                       Class<? extends Job> batchJobClass) throws SchedulerException {
        var jobProps = propsMapper.apply(props);
        var thisJobKey = context.getJobDetail().getKey();
        jobProps.put(PROPS_JOB_RETURN_NAME, thisJobKey.getName());
        if (thisJobKey.getGroup() != null) {
            jobProps.put(PROPS_JOB_RETURN_GROUP, thisJobKey.getGroup());
        }
        var jobKey = JobKey.jobKey(key, UUID.randomUUID().toString());
        scheduler.addJob(JobBuilder.newJob(batchJobClass).withIdentity(jobKey).storeDurably().usingJobData(jobProps).build(), false);
        scheduler.triggerJob(jobKey);
    }

    public static void triggerReturnJob(JobExecutionContext context,
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

    public static String getLastJobKey(JobDataMap props) {
        return props.containsKey(PROPS_JOB_LAST) ? props.getString(PROPS_JOB_LAST) : null;
    }
}
