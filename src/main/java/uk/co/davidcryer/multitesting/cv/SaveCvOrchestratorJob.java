package uk.co.davidcryer.multitesting.cv;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.OrchestratorJob;
import uk.co.davidcryer.quartz.Task;

import java.util.List;

import static uk.co.davidcryer.quartz.TaskUtils.pass;

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
                Task.builder()
                        .key(StoreCvTaskJob.KEY)
                        .propsMapper(pass("cv", StoreCvTaskJob::props))
                        .build(),
                Task.Batch.batchBuilder()
                        .key(PublishCvTaskJob.KEY)
                        .propsMapper(StoreCvTaskJob.returnPropsMapper(PublishCvTaskJob::props))
                        .batchJobClass(PublishCvTaskJob.class)
                        .build(),
                Task.builder()
                        .key(UpdateCvWithPublishStatusTaskJob.KEY)
                        .propsMapper(PublishCvTaskJob.returnPropsMapper(UpdateCvWithPublishStatusTaskJob::props))
                        .build()
        );
    }

    public static JobDataMap props(String request) {
        var props = new JobDataMap();
        props.put("cv", request);
        return props;
    }
}
