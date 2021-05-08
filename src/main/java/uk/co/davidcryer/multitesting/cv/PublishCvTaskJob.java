package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.Task;
import uk.co.davidcryer.quartz.TaskBatchJob;

import java.util.List;
import java.util.function.Function;

import static uk.co.davidcryer.quartz.TaskUtils.pass;

@Component
public class PublishCvTaskJob extends TaskBatchJob {
    public static final String KEY = "publish-cv";

    @Autowired
    public PublishCvTaskJob(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected List<Task> getTasks() {
        return List.of(
                new Task(PublishCvToClientTaskJob.KEY, pass("cvId", PublishCvToClientTaskJob::props)),
                new Task.Batch(PublishCvToKafkaTaskBatchJob.KEY, pass("cvId", PublishCvToKafkaTaskJob::props), PublishCvToKafkaTaskBatchJob.class)
        );
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }

    @Override
    public void writeToReturnProps(JobExecutionContext context, JobDataMap returnProps) {
        returnProps.put("cvId", context.getJobDetail().getJobDataMap().getString("cvId"));
    }

    public static Function<JobDataMap, JobDataMap> returnPropsMapper(Function<String, JobDataMap> map) {
        return pass("cvId", map);
    }
}
