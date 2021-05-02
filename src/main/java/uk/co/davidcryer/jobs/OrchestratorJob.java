package uk.co.davidcryer.jobs;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class OrchestratorJob implements Job {
    private final Scheduler scheduler;
    private final String key;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var props = context.getMergedJobDataMap();
        try {
            getWorkflow(props).execute(props);
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    private Workflow getWorkflow(JobDataMap props) throws JobExecutionException {
        var lastJob = props.containsKey("job.last") ? props.getString("job.last") : "";
        var workflow = getWorkflowMap().get(lastJob);
        if (workflow != null) {
            return workflow;
        }
        throw new JobExecutionException("Workflow does not exist for last job " + lastJob);
    }

    protected abstract Map<String, Workflow> getWorkflowMap();

    protected void triggerJob(String name, JobDataMap props, boolean setNextJob) throws SchedulerException {
        if (setNextJob) {
            props.put("job.next", key);
        }
        scheduler.triggerJob(JobKey.jobKey(name), props);
    }

    public interface Workflow {
        void execute(JobDataMap props) throws SchedulerException;
    }
}
