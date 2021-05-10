package uk.co.davidcryer.multitesting.cv;

import org.quartz.*;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.Task;
import uk.co.davidcryer.quartz.TaskBatchJob;

import java.util.List;

import static uk.co.davidcryer.quartz.PropsUtils.pass;

@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class PublishCvToKafkaTaskBatchJob extends TaskBatchJob {
    public static final String KEY = "publish-to-kafka-tasks";

    public PublishCvToKafkaTaskBatchJob(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected List<Task> getTasks(JobExecutionContext context) {
        var props = context.getMergedJobDataMap();
        return List.of(
                Task.builder()
                        .key(PublishCvToKafkaTaskJob.KEY)
                        .propsSupplier(pass(props, "cvId", PublishCvToKafkaTaskJob::props))
                        .build(),
                Task.builder()
                        .key(NoOpJob.KEY)
                        .build()
        );
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }
}
