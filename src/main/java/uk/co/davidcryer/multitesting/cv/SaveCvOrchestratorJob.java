package uk.co.davidcryer.multitesting.cv;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.ConcurrentTasks;
import uk.co.davidcryer.quartz.OrchestratorJob;
import uk.co.davidcryer.quartz.Task;

import java.util.List;

@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SaveCvOrchestratorJob extends OrchestratorJob {
    public static final String KEY = "save-cv-orchestrator";

    public SaveCvOrchestratorJob(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    protected List<Task> getTasks() {
        return List.of(
                new Task(StoreCvTaskJob.KEY, SaveCvOrchestratorJob::mapToStoreCvProps),
                new ConcurrentTasks(PublishCvTaskJob.KEY, SaveCvOrchestratorJob::mapToPublishCvProps, PublishCvTaskJob.class),
                new Task(UpdateCvWithPublishStatusTaskJob.KEY, SaveCvOrchestratorJob::mapToUpdateCvWithPublishStatusProps)
        );
    }

    private static JobDataMap mapToStoreCvProps(JobDataMap props) {
        var cv = props.getString("cv");
        return StoreCvTaskJob.props(cv);
    }

    private static JobDataMap mapToPublishCvProps(JobDataMap props) {
        return StoreCvTaskJob.mapReturnProps(props, PublishCvTaskJob::props);
    }

    private static JobDataMap mapToUpdateCvWithPublishStatusProps(JobDataMap props) {
        return PublishCvTaskJob.mapReturnProps(props, UpdateCvWithPublishStatusTaskJob::props);
    }

    public static JobDataMap props(String request) {
        var props = new JobDataMap();
        props.put("cv", request);
        return props;
    }
}
