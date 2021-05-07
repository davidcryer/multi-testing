package uk.co.davidcryer.quartz;

import lombok.RequiredArgsConstructor;
import org.quartz.*;

@RequiredArgsConstructor
public abstract class AbstractTaskJob implements Job {
    static final String PROPS_JOB_LAST = "job.last";
    static final String PROPS_JOB_NEXT_NAME = "job.next.name";
    static final String PROPS_JOB_NEXT_GROUP = "job.next.group";
    final Scheduler scheduler;
    final String key;

    protected void triggerNextJob(JobExecutionContext context) throws JobExecutionException {
        try {
            var props = context.getMergedJobDataMap();
            if (props.containsKey(PROPS_JOB_NEXT_NAME)) {
                var nextProps = new JobDataMap();
                nextProps.put(PROPS_JOB_LAST, key);
                writeToReturnProps(context, nextProps);
                var nextJobKey = getNextJobKey(props);
                scheduler.triggerJob(nextJobKey, nextProps);
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    private JobKey getNextJobKey(JobDataMap props) {
        var name = props.getString(PROPS_JOB_NEXT_NAME);
        if (props.containsKey(PROPS_JOB_NEXT_GROUP)) {
            var group = props.getString(PROPS_JOB_NEXT_GROUP);
            return JobKey.jobKey(name, group);
        }
        return JobKey.jobKey(name);
    }

    protected void writeToReturnProps(JobExecutionContext context, JobDataMap props) {

    }
}
