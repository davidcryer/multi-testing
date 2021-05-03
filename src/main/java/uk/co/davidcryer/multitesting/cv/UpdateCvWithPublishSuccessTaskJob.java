package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.jobs.TaskJob;

@Component
public class UpdateCvWithPublishSuccessTaskJob extends TaskJob {
    public static final String KEY = "update-cv-with-publish-success";
    private final UpdateCvWithPublishSuccessTaskService service;

    public UpdateCvWithPublishSuccessTaskJob(Scheduler scheduler, UpdateCvWithPublishSuccessTaskService service) {
        super(scheduler, KEY);
        this.service = service;
    }

    @Override
    protected void executeTask(JobExecutionContext context) {
        var props = context.getMergedJobDataMap();
        var cvId = props.getString("cvId");
        var didPublishToClient = props.getBoolean("didPublish.client");
        var didPublishToKafka = props.getBoolean("didPublish.kafka");
        service.updateCv(cvId, didPublishToClient, didPublishToKafka);
    }

    public static JobDataMap props(String cvId, boolean didPublishToClient, boolean didPublishToKafka) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        props.put("didPublish.client", didPublishToClient);
        props.put("didPublish.kafka", didPublishToKafka);
        return props;
    }
}
