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
        try {
            executeTask(context);
            var props = context.getMergedJobDataMap();
            if (props.containsKey("task.nextJob")) {
                var nextJob = props.getString("task.nextJob");
                var nextProps = OrchestratorJob.buildProps(key);
                writeToReturnProps(nextProps);
                scheduler.triggerJob(JobKey.jobKey(nextJob), nextProps);
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    protected abstract void executeTask(JobExecutionContext context);

    protected abstract void writeToReturnProps(JobDataMap props);

    protected String key() {
        return key;
    }
}
