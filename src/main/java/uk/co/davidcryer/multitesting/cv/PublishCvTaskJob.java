package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.jobs.ConcurrentTasksJob;

import java.util.List;
import java.util.function.Function;

@Component
public class PublishCvTaskJob extends ConcurrentTasksJob {
    public static final String KEY = "publish-cv";

    @Autowired
    public PublishCvTaskJob(Scheduler scheduler) {
        super(scheduler, KEY);
    }

    @Override
    protected List<Task> getTasks() {
        return List.of(
                new Task(PublishCvToClientTaskJob.KEY, PublishCvTaskJob::mapToPublishCvToClientProps),
                new ConcurrentTask(PublishToKafkaConcurrentTasksJob.KEY, PublishCvTaskJob::mapToPublishCvToKafkaProps, PublishToKafkaConcurrentTasksJob.class)
        );
    }

    private static JobDataMap mapToPublishCvToClientProps(JobDataMap props) {
        var cvId = props.getString("cvId");
        return PublishCvToClientTaskJob.props(cvId);
    }

    private static JobDataMap mapToPublishCvToKafkaProps(JobDataMap props) {
        var cvId = props.getString("cvId");
        return PublishToKafkaConcurrentTasksJob.props(cvId);
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }

    @Override
    protected void writeToReturnProps(JobExecutionContext context, JobDataMap props) {
        props.put("cvId", context.getJobDetail().getJobDataMap().getString("cvId"));
    }

    public static JobDataMap mapReturnProps(JobDataMap props, Function<String, JobDataMap> function) {
        var cvId = props.getString("cvId");
        return function.apply(cvId);
    }
}
