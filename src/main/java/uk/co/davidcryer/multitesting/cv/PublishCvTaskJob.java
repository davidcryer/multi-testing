package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.multitesting.job.TaskJob;

@Component
public class PublishCvTaskJob extends TaskJob {
    public static final String KEY = "publish-cv";
    private final PublishCvTaskService service;

    @Autowired
    public PublishCvTaskJob(Scheduler scheduler, PublishCvTaskService service) {
        super(scheduler, KEY);
        this.service = service;
    }

    @Override
    public void executeTask(JobExecutionContext context) throws JobExecutionException {
        var props = context.getMergedJobDataMap();
        var cvId = props.getString("cvId");
        service.add(cvId);
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }
}



