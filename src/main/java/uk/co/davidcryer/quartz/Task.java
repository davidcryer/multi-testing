package uk.co.davidcryer.quartz;

import lombok.Builder;
import lombok.Getter;
import org.quartz.*;

import java.util.Optional;
import java.util.function.Supplier;

@Getter
public class Task {
    private final String key;
    private final Supplier<JobDataMap> propsSupplier;
    private final Boolean allowedToError;
    private final Runnable errorRecovery;
    private final Runnable onReturnListener;

    @Builder
    public Task(String key,
                Supplier<JobDataMap> propsSupplier,
                Boolean allowedToError,
                Runnable errorRecovery,
                Runnable onReturnListener) {
        this.key = Optional.ofNullable(key).orElseThrow(() -> new RuntimeException("Task key cannot be null"));
        this.propsSupplier = Optional.ofNullable(propsSupplier).orElse(JobDataMap::new);
        this.allowedToError = Optional.ofNullable(allowedToError).orElse(false);
        this.errorRecovery = Optional.ofNullable(errorRecovery).orElse(() -> {});
        this.onReturnListener = Optional.ofNullable(onReturnListener).orElse(() -> {});
    }

    public void triggerJob(JobExecutionContext context, Scheduler scheduler) throws SchedulerException {
        JobUtils.triggerJob(context, scheduler, key, propsSupplier);
    }

    public static class Batch extends Task {
        private final Class<? extends Job> batchJobClass;

        @Builder(builderMethodName = "batchBuilder")
        public Batch(String key,
                     Supplier<JobDataMap> propsSupplier,
                     Boolean allowedToError,
                     Runnable errorRecovery,
                     Runnable onReturnListener,
                     Class<? extends Job> batchJobClass) {
            super(key, propsSupplier, allowedToError, errorRecovery, onReturnListener);
            this.batchJobClass = Optional.ofNullable(batchJobClass).orElseThrow(() -> new RuntimeException("Batch task class cannot be null"));
        }

        @Override
        public void triggerJob(JobExecutionContext context, Scheduler scheduler) throws SchedulerException {
            JobUtils.triggerBatchJob(context, scheduler, getKey(), getPropsSupplier(), batchJobClass);
        }
    }
}
