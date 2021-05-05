package uk.co.davidcryer.jobs;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

public interface Workflow {
    void execute(JobExecutionContext context, JobDataMap props) throws SchedulerException;
}
