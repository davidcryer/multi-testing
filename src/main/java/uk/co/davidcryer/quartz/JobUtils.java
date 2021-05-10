package uk.co.davidcryer.quartz;

import org.quartz.*;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class JobUtils {
    private static final String PROPS_JOB_LAST = "job.last";
    private static final String PROPS_JOB_RETURN_NAME = "job.return.name";
    private static final String PROPS_JOB_RETURN_GROUP = "job.return.group";

    public static void triggerJob(JobExecutionContext context,
                                  Scheduler scheduler,
                                  String key,
                                  Supplier<JobDataMap> propsSupplier) throws SchedulerException {
        var triggerProps = propsSupplier.get();
        putReturnKey(context, triggerProps);
        scheduler.triggerJob(JobKey.jobKey(key), triggerProps);
    }

    public static void triggerBatchJob(JobExecutionContext context,
                                       Scheduler scheduler,
                                       String key,
                                       Supplier<JobDataMap> propsSupplier,
                                       Class<? extends Job> batchJobClass) throws SchedulerException {
        var jobProps = propsSupplier.get();
        putReturnKey(context, jobProps);
        var jobKey = JobKey.jobKey(key, UUID.randomUUID().toString());
        scheduler.addJob(JobBuilder.newJob(batchJobClass)
                        .withIdentity(jobKey)
                        .storeDurably()
                        .usingJobData(jobProps)
                        .build(),
                false);
        scheduler.triggerJob(jobKey);
    }

    private static void putReturnKey(JobExecutionContext context, JobDataMap props) {
        var key = context.getJobDetail().getKey();
        props.put(PROPS_JOB_RETURN_NAME, key.getName());
        if (key.getGroup() != null) {
            props.put(PROPS_JOB_RETURN_GROUP, key.getGroup());
        }
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

    public static String getLastJobKey(JobExecutionContext context) {
        var props = context.getTrigger().getJobDataMap();
        return props.containsKey(PROPS_JOB_LAST) ? props.getString(PROPS_JOB_LAST) : null;
    }
}
