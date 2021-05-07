package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.TaskJob;

@Component
public class PublishCvToKafkaTaskJob extends TaskJob {
    public static final String KEY = "publish-cv-to-kafka";
    private final PublishCvToKafkaTaskService service;

    @Autowired
    public PublishCvToKafkaTaskJob(Scheduler scheduler, PublishCvToKafkaTaskService service) {
        super(scheduler);
        this.service = service;
    }

    @Override
    public void executeTask(JobExecutionContext context) {
        var cvId = context.getMergedJobDataMap().getString("cvId");
        service.add(cvId);
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }
}



