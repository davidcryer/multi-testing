package uk.co.davidcryer.multitesting.cv;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.Task;
import uk.co.davidcryer.quartz.TaskBatchJob;

import java.util.List;

import static uk.co.davidcryer.quartz.TaskUtils.pass;

@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class PublishCvToKafkaTaskBatchJob extends TaskBatchJob {
    public static final String KEY = "publish-to-kafka-tasks";

    public PublishCvToKafkaTaskBatchJob(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected List<Task> getTasks() {
        return List.of(
                new Task(PublishCvToKafkaTaskJob.KEY, pass("cvId", PublishCvToKafkaTaskJob::props)),
                new Task(NoOpJob.KEY, JobDataMap::new)
        );
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }
}
