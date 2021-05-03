package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.jobs.TaskJob;

import java.util.function.Function;

@Component
public class PublishCvToClientTaskJob extends TaskJob {
    public static final String KEY = "publish-cv-to-client";
    private final PublishCvToClientTaskService service;

    @Autowired
    public PublishCvToClientTaskJob(Scheduler scheduler, PublishCvToClientTaskService service) {
        super(scheduler, KEY);
        this.service = service;
    }

    @Override
    protected void executeTask(JobExecutionContext context) throws JobExecutionException {
        var props = context.getMergedJobDataMap();
        var cvId = props.getString("cvId");
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
