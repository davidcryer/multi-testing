package uk.co.davidcryer.quartz;

import lombok.Builder;
import lombok.Getter;
import org.quartz.*;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
public class Task {
    private final String key;
    private final Function<JobDataMap, JobDataMap> propsMapper;
    private final Boolean allowedToError;
    private final Runnable errorRecovery;
    private final BiConsumer<JobDataMap, JobDataMap> returnPropsConsumer;

    @Builder
    public Task(String key,
                Function<JobDataMap, JobDataMap> propsMapper,
                Boolean allowedToError,
                Runnable errorRecovery,
                BiConsumer<JobDataMap, JobDataMap> returnPropsConsumer) {
        this.key = Optional.ofNullable(key).orElseThrow(() -> new RuntimeException("Task key cannot be null"));
        this.propsMapper = Optional.ofNullable(propsMapper).orElse(ignore -> new JobDataMap());
        this.allowedToError = Optional.ofNullable(allowedToError).orElse(false);
        this.errorRecovery = Optional.ofNullable(errorRecovery).orElse(() -> {});
        this.returnPropsConsumer = Optional.ofNullable(returnPropsConsumer).orElse((props, jobProps) -> {});
    }

    public void triggerJob(JobExecutionContext context, JobDataMap props, Scheduler scheduler) throws SchedulerException {
        JobUtils.triggerJob(context, props, scheduler, key, propsMapper);
    }

    public static class Batch extends Task {
        private final Class<? extends Job> batchJobClass;

        @Builder(builderMethodName = "batchBuilder")
        public Batch(String key,
                     Function<JobDataMap, JobDataMap> propsMapper,
                     Boolean allowedToError,
                     Runnable errorRecovery,
                     BiConsumer<JobDataMap, JobDataMap> returnPropsConsumer,
                     Class<? extends Job> batchJobClass) {
            super(key, propsMapper, allowedToError, errorRecovery, returnPropsConsumer);
            this.batchJobClass = Optional.ofNullable(batchJobClass).orElseThrow(() -> new RuntimeException("Batch task class cannot be null"));
        }

        @Override
        public void triggerJob(JobExecutionContext context, JobDataMap props, Scheduler scheduler) throws SchedulerException {
            JobUtils.triggerBatchJob(context, props, scheduler, getKey(), getPropsMapper(), batchJobClass);
        }
    }
}
