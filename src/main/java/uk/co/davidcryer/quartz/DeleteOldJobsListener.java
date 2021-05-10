package uk.co.davidcryer.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.listeners.JobListenerSupport;

import java.util.Set;

import static uk.co.davidcryer.quartz.JobExecutionContextUtils.getJobName;
import static uk.co.davidcryer.quartz.TaskUtils.isErrored;
import static uk.co.davidcryer.quartz.TaskUtils.isFinished;

@RequiredArgsConstructor
@Slf4j
public class DeleteOldJobsListener extends JobListenerSupport {
    private final Scheduler scheduler;
    private final Set<String> jobsRequiringDeletion;

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        try {
            if (shouldDeleteJob(context, jobException)) {
                var jobKey = context.getJobDetail().getKey();
                log.info("Deleting job {}", jobKey);
                var didDelete = scheduler.deleteJob(jobKey);
                if (!didDelete) {
                    log.error("Failed to delete job {}", jobKey);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private boolean shouldDeleteJob(JobExecutionContext context, JobExecutionException jobException) {
        var jobProps = context.getJobDetail().getJobDataMap();
        return jobsRequiringDeletion.contains(getJobName(context)) &&
                (jobException != null || isFinished(jobProps) || isErrored(jobProps));
    }
}
