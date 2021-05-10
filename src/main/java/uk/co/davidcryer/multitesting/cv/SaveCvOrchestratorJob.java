package uk.co.davidcryer.multitesting.cv;

import org.quartz.*;
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
    protected List<Task> getTasks(JobExecutionContext context) {
        var props = context.getMergedJobDataMap();
        var jobProps = context.getJobDetail().getJobDataMap();
        return List.of(
                Task.builder()
                        .key(StoreCvTaskJob.KEY)
                        .propsSupplier(pass(props, "cv", StoreCvTaskJob::props))
                        .onReturnListener(() -> {
                            StoreCvTaskJob.withReturnProps(props, cvId -> jobProps.put("cvId", cvId));
                        })
                        .build(),
                Task.Batch.batchBuilder()
                        .key(PublishCvTaskJob.KEY)
                        .propsSupplier(pass(jobProps, "cvId", PublishCvTaskJob::props))
                        .batchJobClass(PublishCvTaskJob.class)
                        .build(),
                Task.builder()
                        .key(UpdateCvWithPublishStatusTaskJob.KEY)
                        .propsSupplier(pass(jobProps, "cvId", UpdateCvWithPublishStatusTaskJob::props))
                        .build()
        );
    }

    public static JobDataMap props(String request) {
        var props = new JobDataMap();
        props.put("cv", request);
        return props;
    }
}
