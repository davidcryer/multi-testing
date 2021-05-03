package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.jobs.TaskJob;

import java.util.function.Function;

@Component
public class PublishCvToKafkaTaskJob extends TaskJob {
    public static final String KEY = "publish-cv-to-kafka";
    private final PublishCvToKafkaTaskService service;

    @Autowired
    public PublishCvToKafkaTaskJob(Scheduler scheduler, PublishCvToKafkaTaskService service) {
        super(scheduler, KEY);
        this.service = service;
    }

    @Override
    public void executeTask(JobExecutionContext context) {
        var cvId = context.getMergedJobDataMap().getString("cvId");
        service.add(cvId);
    }

    @Override
    protected void writeToReturnProps(JobExecutionContext context, JobDataMap props) {
        props.put("cvId", context.getMergedJobDataMap().getString("cvId"));
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }

    public static JobDataMap mapReturnProps(JobDataMap props, Function<String, JobDataMap> function) {
        var cvId = props.getString("cvId");
        return function.apply(cvId);
    }
}



