package uk.co.davidcryer.multitesting.job;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class TaskJob implements Job {
    private final Scheduler scheduler;
    private final String key;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        executeTask(context);
        triggerNextJob(context);
    }

    private void triggerNextJob(JobExecutionContext context) throws JobExecutionException {
        try {
            var props = context.getMergedJobDataMap();
            if (props.containsKey("job.next")) {
                var nextJob = props.getString("job.next");
                var nextProps = new JobDataMap();
                nextProps.put("job.last", key);
                writeToReturnProps(nextProps);
                scheduler.triggerJob(JobKey.jobKey(nextJob), nextProps);
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    protected abstract void executeTask(JobExecutionContext context) throws JobExecutionException;

    protected void writeToReturnProps(JobDataMap props) {

    }
}
