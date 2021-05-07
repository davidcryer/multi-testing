package uk.co.davidcryer.multitesting.cv;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.OrchestratorJob;

import java.util.Map;

@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SaveCvOrchestratorJob extends OrchestratorJob {
    public static final String KEY = "save-cv-orchestrator";

    public SaveCvOrchestratorJob(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected Map<String, Workflow> getWorkflowMap() {
        return Map.of(
                "", this.triggerStoreCvJob,
                StoreCvTaskJob.KEY, this.triggerPublishJobs,
                PublishCvTaskJob.KEY, this.triggerUpdateCvWithPublishStatusJob,
                UpdateCvWithPublishStatusTaskJob.KEY, this::markFinished
        );
    }

    private final Workflow triggerStoreCvJob = (context, props) -> {
        var cv = props.getString("cv");
        var storeCvProps= StoreCvTaskJob.props(cv);
        triggerJob(context, StoreCvTaskJob.KEY, storeCvProps, true);
    };

    private final Workflow triggerPublishJobs = (context, props) -> {
        var publishCvProps = StoreCvTaskJob.mapReturnProps(props, PublishCvTaskJob::props);
        triggerConcurrentJob(context, PublishCvTaskJob.class, PublishCvTaskJob.KEY, publishCvProps, true);
    };

    private final Workflow triggerUpdateCvWithPublishStatusJob = (context, props) -> {
        var updateCvWithPublishProps = PublishCvTaskJob.mapReturnProps(props, UpdateCvWithPublishStatusTaskJob::props);
        triggerJob(context, UpdateCvWithPublishStatusTaskJob.KEY, updateCvWithPublishProps, true);
    };

    public static JobDataMap props(String request) {
        var props = new JobDataMap();
        props.put("cv", request);
        return props;
    }
}
