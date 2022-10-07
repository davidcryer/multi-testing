package uk.co.davidcryer.quartz;

import org.quartz.*;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class Task {
    private final String key;
    private final Supplier<JobDataMap> propsSupplier;
    private final Boolean allowedToError;
    private final Runnable errorRecovery;
    private final Runnable onReturnListener;

    public Task(String key,
                Supplier<JobDataMap> propsSupplier,
                Boolean allowedToError,
                Runnable errorRecovery,
                Runnable onReturnListener) {
        this.key = Objects.requireNonNull(key, "Task key cannot be null");
        this.propsSupplier = Objects.requireNonNullElse(propsSupplier, JobDataMap::new);
        this.allowedToError = Objects.requireNonNullElse(allowedToError, false);
        this.errorRecovery = Objects.requireNonNullElse(errorRecovery, () -> {});
        this.onReturnListener = Objects.requireNonNullElse(onReturnListener, () -> {});
    }

    public void triggerJob(JobExecutionContext context, Scheduler scheduler) throws SchedulerException {
        JobUtils.triggerJob(context, scheduler, key, propsSupplier);
    }

    public String getKey() {
        return key;
    }

    public Supplier<JobDataMap> getPropsSupplier() {
        return propsSupplier;
    }

    public Boolean getAllowedToError() {
        return allowedToError;
    }

    public Runnable getErrorRecovery() {
        return errorRecovery;
    }

    public Runnable getOnReturnListener() {
        return onReturnListener;
    }

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    public static TaskBatchBuilder batchBuilder() {
        return new TaskBatchBuilder();
    }

    public static class TaskBuilder {
        private String key;
        private Supplier<JobDataMap> propsSupplier;
        private Boolean allowedToError;
        private Runnable errorRecovery;
        private Runnable onReturnListener;

        private TaskBuilder() {}

        public TaskBuilder key(String key) {
            this.key = key;
            return this;
        }

        public TaskBuilder propsSupplier(Supplier<JobDataMap> propsSupplier) {
            this.propsSupplier = propsSupplier;
            return this;
        }

        public TaskBuilder allowedToError(Boolean allowedToError) {
            this.allowedToError = allowedToError;
            return this;
        }

        public TaskBuilder errorRecovery(Runnable errorRecovery) {
            this.errorRecovery = errorRecovery;
            return this;
        }

        public TaskBuilder onReturnListener(Runnable onReturnListener) {
            this.onReturnListener = onReturnListener;
            return this;
        }

        public Task build() {
            return new Task(key, propsSupplier, allowedToError, errorRecovery, onReturnListener);
        }
    }

    public static class Batch extends Task {
        private final Class<? extends Job> batchJobClass;

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

    public static class TaskBatchBuilder {
        private String key;
        private Supplier<JobDataMap> propsSupplier;
        private Boolean allowedToError;
        private Runnable errorRecovery;
        private Runnable onReturnListener;
        private Class<? extends Job> batchJobClass;

        private TaskBatchBuilder() {}

        public Task.Batch.TaskBatchBuilder key(String key) {
            this.key = key;
            return this;
        }

        public TaskBatchBuilder propsSupplier(Supplier<JobDataMap> propsSupplier) {
            this.propsSupplier = propsSupplier;
            return this;
        }

        public TaskBatchBuilder allowedToError(Boolean allowedToError) {
            this.allowedToError = allowedToError;
            return this;
        }

        public TaskBatchBuilder errorRecovery(Runnable errorRecovery) {
            this.errorRecovery = errorRecovery;
            return this;
        }

        public TaskBatchBuilder onReturnListener(Runnable onReturnListener) {
            this.onReturnListener = onReturnListener;
            return this;
        }

        public TaskBatchBuilder batchJobClass(Class<? extends Job> batchJobClass) {
            this.batchJobClass = batchJobClass;
            return this;
        }

        public Task build() {
            return new Task.Batch(key, propsSupplier, allowedToError, errorRecovery, onReturnListener, batchJobClass);
        }
    }
}
