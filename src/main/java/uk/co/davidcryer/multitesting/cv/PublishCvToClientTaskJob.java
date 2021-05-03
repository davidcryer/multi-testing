package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.jobs.TaskJob;

import java.util.function.BiFunction;

@Component
public class PublishCvToClientTaskJob extends TaskJob {
    public static final String KEY = "publish-cv-to-client";
    private final PublishCvToClientTaskService service;
    private boolean didPublish;

    @Autowired
    public PublishCvToClientTaskJob(Scheduler scheduler, PublishCvToClientTaskService service) {
        super(scheduler, KEY);
        this.service = service;
    }

    @Override
    protected void executeTask(JobExecutionContext context) throws JobExecutionException {
        var props = context.getMergedJobDataMap();
        var cvId = props.getString("cvId");
        didPublish = service.add(cvId);
    }

    @Override
    protected void writeToReturnProps(JobExecutionContext context, JobDataMap props) {
        props.put("cvId", context.getMergedJobDataMap().getString("cvId"));
        props.put("didPublish", didPublish);
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }

    public static JobDataMap mapReturnProps(JobDataMap props, BiFunction<String, Boolean, JobDataMap> function) {
        var cvId = props.getString("cvId");
        var didPublish = props.getBoolean("didPublish");
        return function.apply(cvId, didPublish);
    }
}
