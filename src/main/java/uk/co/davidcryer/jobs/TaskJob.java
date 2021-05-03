package uk.co.davidcryer.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public abstract class TaskJob implements Job {
    static final String PROPS_JOB_LAST = "job.last";
    static final String PROPS_JOB_NEXT = "job.next";
    private final Scheduler scheduler;
    private final String key;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("executing job {}", key);
        executeTask(context);
        triggerNextJob(context);
    }

    private void triggerNextJob(JobExecutionContext context) throws JobExecutionException {
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

    protected abstract void executeTask(JobExecutionContext context) throws JobExecutionException;

    protected void writeToReturnProps(JobExecutionContext context, JobDataMap props) {

    }
}
