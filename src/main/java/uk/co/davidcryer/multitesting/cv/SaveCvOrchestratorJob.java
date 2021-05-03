package uk.co.davidcryer.multitesting.cv;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.jobs.OrchestratorJob;

import java.util.Map;

@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SaveCvOrchestratorJob extends OrchestratorJob {
    public static final String KEY = "save-cv-orchestrator";

    public SaveCvOrchestratorJob(Scheduler scheduler) {
        super(scheduler, KEY);
    }

    @Override
    protected Map<String, Workflow> getWorkflowMap() {
        return Map.of(
                "", this.triggerStoreCvJob,
                StoreCvTaskJob.KEY, this.triggerPublishJobs,
                PublishCvToClientTaskJob.KEY, (context, props) -> {
                    var jobProps = context.getJobDetail().getJobDataMap();
                    jobProps.put("clientPublishJobFinished", true);
                    if (jobProps.containsKey("kafkaPublishJobFinished")) {
                        this.triggerUpdateCvWithPublishStatusJob.execute(context, props);
                    }
                },
                PublishCvToKafkaTaskJob.KEY, (context, props) -> {
                    var jobProps = context.getJobDetail().getJobDataMap();
                    jobProps.put("kafkaPublishJobFinished", true);
                    if (jobProps.containsKey("clientPublishJobFinished")) {
                        this.triggerUpdateCvWithPublishStatusJob.execute(context, props);
                    }
                }
        );
    }

    private final Workflow triggerStoreCvJob = (context, props) -> {
        var cv = props.getString("cv");
        var storeCvProps= StoreCvTaskJob.props(cv);
        triggerJob(StoreCvTaskJob.KEY, storeCvProps, true);
    };

    private final Workflow triggerPublishJobs = (context, props) -> {
        var clientPublishProps = StoreCvTaskJob.mapReturnProps(props, PublishCvToClientTaskJob::props);
        triggerJob(PublishCvToClientTaskJob.KEY, clientPublishProps, true);

        var kafkaPublishProps = PublishCvToClientTaskJob.mapReturnProps(props, PublishCvToKafkaTaskJob::props);
        triggerJob(PublishCvToKafkaTaskJob.KEY, kafkaPublishProps, true);
    };

    private final Workflow triggerUpdateCvWithPublishStatusJob = (context, props) -> {
        var updateCvWithPublishProps = PublishCvToKafkaTaskJob.mapReturnProps(props, UpdateCvWithPublishStatusTaskJob::props);
        triggerJob(UpdateCvWithPublishStatusTaskJob.KEY, updateCvWithPublishProps, false);
    };

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