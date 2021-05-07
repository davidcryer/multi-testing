package uk.co.davidcryer.multitesting.cv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.listeners.JobListenerSupport;

import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class DeleteOldJobsListener extends JobListenerSupport {
    private final Scheduler scheduler;
    private final Set<String> jobsRequiringCleanup;

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        try {
            if (shouldDeleteJob(context)) {
                log.info("Deleting job {}", context.getJobDetail().getKey());
                scheduler.deleteJob(context.getJobDetail().getKey());
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private boolean shouldDeleteJob(JobExecutionContext context) {
        var jobName = context.getJobDetail().getKey().getName();
        var jobProps = context.getJobDetail().getJobDataMap();
        return jobsRequiringCleanup.contains(jobName)
                && jobProps.containsKey("isFinished")
                && jobProps.getBoolean("isFinished");
    }
}
