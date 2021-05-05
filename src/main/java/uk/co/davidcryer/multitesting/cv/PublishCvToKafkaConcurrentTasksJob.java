package uk.co.davidcryer.multitesting.cv;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.jobs.ConcurrentTasksJob;

import java.util.List;

@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class PublishCvToKafkaConcurrentTasksJob extends ConcurrentTasksJob {
    public static final String KEY = "publish-to-kafka-tasks";

    public PublishCvToKafkaConcurrentTasksJob(Scheduler scheduler) {
        super(scheduler, KEY);
    }

    @Override
    protected List<Task> getTasks() {
        return List.of(
                new Task(PublishCvToKafkaTaskJob.KEY, PublishCvToKafkaConcurrentTasksJob::mapToPublishCvToKafkaProps),
                new Task(NoOpJob.KEY, JobDataMap::new)
        );
    }

    private static JobDataMap mapToPublishCvToKafkaProps(JobDataMap props) {
        var cvId = props.getString("cvId");
        return PublishCvToKafkaTaskJob.props(cvId);
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }
}
