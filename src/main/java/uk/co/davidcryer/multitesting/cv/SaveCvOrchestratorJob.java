package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.jobs.OrchestratorJob;

import java.util.Map;

@Component
public class SaveCvOrchestratorJob extends OrchestratorJob {
    public static final String KEY = "save-cv-orchestrator";

    public SaveCvOrchestratorJob(Scheduler scheduler) {
        super(scheduler, KEY);
    }

    @Override
    protected Map<String, Workflow> getWorkflowMap() {
        return Map.of(
                "", props -> {
                    var cv = props.getString("cv");
                    var nextProps= StoreCvTaskJob.props(cv);
                    triggerJob(StoreCvTaskJob.KEY, nextProps, true);
                },
                StoreCvTaskJob.KEY, props -> {
                    var nextProps = StoreCvTaskJob.mapReturnProps(props, PublishCvTaskJob::props);
                    triggerJob(PublishCvTaskJob.KEY, nextProps, false);
                }
        );
    }

    public static JobDataMap props(String request) {
        var props = new JobDataMap();
        props.put("cv", request);
        return props;
    }
}

// TODO could drive via json-defined workflow
/*
{
    "jobName": "store-cv",
    "nextJob": {
        "jobName": "publish-cv",
        "nextJob": null,
        "jobAdaptor": null
    },
    "jobAdaptor": "StoreCvToPublishCvTasksAdaptor"
}
 */