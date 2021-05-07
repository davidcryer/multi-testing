package uk.co.davidcryer.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.UUID;

import static uk.co.davidcryer.quartz.AbstractTaskJob.*;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public abstract class OrchestratorJob implements Job, MarkableAsFinished {
    private final Scheduler scheduler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var props = context.getMergedJobDataMap();
        try {
            getWorkflow(props).execute(context, props);
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    private Workflow getWorkflow(JobDataMap props) throws JobExecutionException {
        var lastJob = props.containsKey(PROPS_JOB_LAST) ? props.getString(PROPS_JOB_LAST) : "";
        log.info("executing orchestrator with last job {}", lastJob);
        var workflow = getWorkflowMap().get(lastJob);
        if (workflow != null) {
            return workflow;
        }
        throw new JobExecutionException("Workflow does not exist for last job " + lastJob);
    }

    protected abstract Map<String, Workflow> getWorkflowMap();

    protected void triggerJob(JobExecutionContext context, String name, JobDataMap props, boolean setNextJob) throws SchedulerException {
        if (setNextJob) {
            var jobKey = context.getJobDetail().getKey();
            props.put(PROPS_JOB_NEXT_NAME, jobKey.getName());
            if (jobKey.getGroup() != null) {
                props.put(PROPS_JOB_NEXT_GROUP, jobKey.getGroup());
            }
        }
        scheduler.triggerJob(JobKey.jobKey(name), props);
    }

    protected void triggerConcurrentJob(JobExecutionContext context, Class<? extends Job> clazz, String name, JobDataMap props, boolean setNextJob) throws SchedulerException {
        if (setNextJob) {
            var thisJobKey = context.getJobDetail().getKey();
            props.put(PROPS_JOB_NEXT_NAME, thisJobKey.getName());
            if (thisJobKey.getGroup() != null) {
                props.put(PROPS_JOB_NEXT_GROUP, thisJobKey.getGroup());
            }
        }
        var jobKey = JobKey.jobKey(name, UUID.randomUUID().toString());
        scheduler.addJob(JobBuilder.newJob(clazz).withIdentity(jobKey).storeDurably().usingJobData(props).build(), false);
        scheduler.triggerJob(jobKey);
    }

    public interface Workflow {
        void execute(JobExecutionContext context, JobDataMap props) throws SchedulerException;
    }
}
