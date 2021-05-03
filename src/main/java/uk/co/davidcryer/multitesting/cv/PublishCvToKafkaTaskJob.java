package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.jobs.TaskJob;

@Component
public class PublishCvToKafkaTaskJob extends TaskJob {
    public static final String KEY = "publish-cv-to-kafka";
    private final PublishCvToKafkaTaskService service;
    private boolean didPublish;

    @Autowired
    public PublishCvToKafkaTaskJob(Scheduler scheduler, PublishCvToKafkaTaskService service) {
        super(scheduler, KEY);
        this.service = service;
    }

    @Override
    public void executeTask(JobExecutionContext context) {
        var cvId = context.getMergedJobDataMap().getString("cvId");
        didPublish = service.add(cvId);
    }

    @Override
    protected void writeToReturnProps(JobExecutionContext context, JobDataMap props) {
        props.put("cvId", context.getMergedJobDataMap().getString("cvId"));
        props.put("didPublish", didPublish);
        props.put("didPublishToClient", context.getMergedJobDataMap().getBoolean("didPublishToClient"));
    }

    public static JobDataMap props(String cvId, Boolean didPublishToClient) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        props.put("didPublishToClient", didPublishToClient);
        return props;
    }

    public static JobDataMap mapReturnProps(JobDataMap props, PropsFunction function) {
        var cvId = props.getString("cvId");
        var didPublish = props.getBoolean("didPublish");
        var didPublishToClient = props.getBoolean("didPublishToClient");
        return function.apply(cvId, didPublish, didPublishToClient);
    }

    interface PropsFunction {
        JobDataMap apply(String cvId, Boolean didPublishToKafka, Boolean didPublishToClient);
    }
}



