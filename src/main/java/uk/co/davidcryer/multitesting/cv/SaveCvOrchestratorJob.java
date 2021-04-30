package uk.co.davidcryer.multitesting.cv;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.multitesting.job.OrchestratorJob;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SaveCvOrchestratorJob extends OrchestratorJob {
    public static final String KEY = "save-cv-orchestrator";
    private final Scheduler scheduler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var props = context.getMergedJobDataMap();
        try {
            var lastJob = getLastJob(props);
            if (lastJob.isEmpty()) {
                var cvRequest = props.getString("request");
                var nextProps = StoreCvTaskJob.buildProps(cvRequest);
                nextProps.put("orchestrator.nextJob", KEY);
                scheduler.triggerJob(JobKey.jobKey(StoreCvTaskJob.KEY), nextProps);//TODO see if reasonable to pass same props through to next job
            } else if (lastJob.equals(StoreCvTaskJob.KEY)) {
                var cvId = props.getString("cvId");
                var nextProps = PublishCvTaskJob.buildProps(cvId);
                scheduler.triggerJob(JobKey.jobKey(PublishCvTaskJob.KEY), nextProps);
            }
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    public static JobDataMap buildProps(String request) {
        var props = new JobDataMap();
        props.put("cv", request);
        return props;
    }

    public static <T> T withProps(JobDataMap props, PropsConsumer<T> consumer) {
        var cvId = props.getString("cv.id");
        return consumer.apply(cvId);
    }

    interface PropsConsumer<T> {
        T apply(String cvId);
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