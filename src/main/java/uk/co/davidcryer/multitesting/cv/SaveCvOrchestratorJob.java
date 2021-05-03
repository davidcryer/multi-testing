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
                    var storeCvProps= StoreCvTaskJob.props(cv);
                    triggerJob(StoreCvTaskJob.KEY, storeCvProps, true);
                },
                StoreCvTaskJob.KEY, props -> {
                    var clientPublishProps = StoreCvTaskJob.mapReturnProps(props, PublishCvToClientTaskJob::props);
                    triggerJob(PublishCvToClientTaskJob.KEY, clientPublishProps, true);
                },
                PublishCvToClientTaskJob.KEY, props -> {
                    var kafkaPublishProps = PublishCvToClientTaskJob.mapReturnProps(props, PublishCvToKafkaTaskJob::props);
                    triggerJob(PublishCvToKafkaTaskJob.KEY, kafkaPublishProps, true);
                },
                PublishCvToKafkaTaskJob.KEY, props -> {
//                    var updateCvWithPublishProps = PublishCvToClientTaskJob.mapReturnProps(props, (cvId, didPublishToClient) ->
//                            PublishCvToKafkaTaskJob.mapReturnProps(props, (ignore, didPublishToKafka) ->
//                                    UpdateCvWithPublishSuccessTaskJob.props(cvId, didPublishToClient, didPublishToKafka)));
                    var updateCvWithPublishProps = PublishCvToKafkaTaskJob.mapReturnProps(props, UpdateCvWithPublishSuccessTaskJob::props);
                    triggerJob(UpdateCvWithPublishSuccessTaskJob.KEY, updateCvWithPublishProps, false);
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