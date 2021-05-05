package uk.co.davidcryer.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.UUID;

import static uk.co.davidcryer.jobs.AbstractTaskJob.PROPS_JOB_LAST;
import static uk.co.davidcryer.jobs.AbstractTaskJob.PROPS_JOB_NEXT;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public abstract class OrchestratorJob implements Job {
    private final Scheduler scheduler;
    private final String key;

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

    protected void triggerJob(String name, JobDataMap props, boolean setNextJob) throws SchedulerException {
        if (setNextJob) {
            props.put(PROPS_JOB_NEXT, key);
        }
        scheduler.triggerJob(JobKey.jobKey(name), props);
    }

    protected void triggerConcurrentJob(Class<? extends Job> clazz, String name, JobDataMap props, boolean setNextJob) throws SchedulerException {
        var jobProps = new JobDataMap();
        if (setNextJob) {
            jobProps.put(PROPS_JOB_NEXT, key);
        }
        name = name + "-" + UUID.randomUUID();
        scheduler.addJob(JobBuilder.newJob(clazz).withIdentity(name).storeDurably().usingJobData(jobProps).build(), false);
        scheduler.triggerJob(JobKey.jobKey(name), props);
    }
}
