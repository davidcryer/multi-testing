package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.jobs.TaskJob;

@Component
public class UpdateCvWithPublishStatusTaskJob extends TaskJob {
    public static final String KEY = "update-cv-with-publish-success";
    private final UpdateCvWithPublishStatusTaskService service;

    public UpdateCvWithPublishStatusTaskJob(Scheduler scheduler, UpdateCvWithPublishStatusTaskService service) {
        super(scheduler, KEY);
        this.service = service;
    }

    @Override
    protected void executeTask(JobExecutionContext context) {
        var props = context.getMergedJobDataMap();
        var cvId = props.getString("cvId");
        service.updateCv(cvId);
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }
}
