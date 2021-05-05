package uk.co.davidcryer.jobs;

import lombok.RequiredArgsConstructor;
import org.quartz.*;

@RequiredArgsConstructor
public abstract class AbstractTaskJob implements Job {
    static final String PROPS_JOB_LAST = "job.last";
    static final String PROPS_JOB_NEXT = "job.next";
    final Scheduler scheduler;
    final String key;

    protected void triggerNextJob(JobExecutionContext context) throws JobExecutionException {
        try {
            var props = context.getMergedJobDataMap();
            if (props.containsKey(PROPS_JOB_NEXT)) {
                var nextJob = props.getString(PROPS_JOB_NEXT);
                var nextProps = new JobDataMap();
                nextProps.put(PROPS_JOB_LAST, key);
                writeToReturnProps(context, nextProps);
                scheduler.triggerJob(JobKey.jobKey(nextJob), nextProps);
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    protected void writeToReturnProps(JobExecutionContext context, JobDataMap props) {

    }
}
