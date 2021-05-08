package uk.co.davidcryer.quartz;

import org.quartz.*;

public interface JobReturn {

    default void triggerReturnJob(JobExecutionContext context, Scheduler scheduler) throws JobExecutionException {
        try {
            JobUtils.triggerReturnJob(context, scheduler, this::writeToReturnProps);
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    default void writeToReturnProps(JobExecutionContext context, JobDataMap returnProps) {}
}
