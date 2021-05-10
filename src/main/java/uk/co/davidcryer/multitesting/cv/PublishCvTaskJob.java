package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.Task;
import uk.co.davidcryer.quartz.TaskBatchJob;

import java.util.List;

import static uk.co.davidcryer.quartz.PropsUtils.pass;

@Component
public class PublishCvTaskJob extends TaskBatchJob {
    public static final String KEY = "publish-cv";

    @Autowired
    public PublishCvTaskJob(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected List<Task> getTasks(JobExecutionContext context) {
        var props = context.getMergedJobDataMap();
        return List.of(
                Task.builder()
                        .key(PublishCvToClientTaskJob.KEY)
                        .propsSupplier(pass(props, "cvId", PublishCvToClientTaskJob::props))
                        .build(),
                Task.Batch.batchBuilder()
                        .key(PublishCvToKafkaTaskBatchJob.KEY)
                        .propsSupplier(pass(props, "cvId", PublishCvToKafkaTaskJob::props))
                        .batchJobClass(PublishCvToKafkaTaskBatchJob.class)
                        .build()
        );
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }
}
