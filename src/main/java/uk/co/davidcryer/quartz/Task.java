package uk.co.davidcryer.quartz;

import lombok.Getter;
import org.quartz.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
public class Task {
    private static final Predicate<JobDataMap> IMPLIED_SUCCESS_PREDICATE = ignore -> true;
    private static final Consumer<JobDataMap> NO_OP_CONSUMER = props -> {};
    private final String key;
    private final Function<JobDataMap, JobDataMap> propsMapper;
    private final Predicate<JobDataMap> successfulJobCondition;
    private final Consumer<JobDataMap> returnPropsWriter;

    public Task(String key, Function<JobDataMap, JobDataMap> propsMapper) {
        this(key, propsMapper, IMPLIED_SUCCESS_PREDICATE, NO_OP_CONSUMER);
    }

    public Task(String key, Function<JobDataMap, JobDataMap> propsMapper, Predicate<JobDataMap> successfulJobCondition) {
        this(key, propsMapper, successfulJobCondition, NO_OP_CONSUMER);
    }

    public Task(String key,
                Function<JobDataMap, JobDataMap> propsMapper,
                Predicate<JobDataMap> successfulJobCondition,
                Consumer<JobDataMap> returnPropsWriter) {
        this.key = key;
        this.propsMapper = propsMapper;
        this.successfulJobCondition = successfulJobCondition;
        this.returnPropsWriter = returnPropsWriter;
    }

    public void triggerJob(JobExecutionContext context, JobDataMap props, Scheduler scheduler) throws SchedulerException {
        TaskUtils.triggerJob(context, props, scheduler, key, propsMapper);
    }

    public static class Batch extends Task {
        private final Class<? extends Job> batchJobClass;

        public Batch(String key,
                         Function<JobDataMap, JobDataMap> propsMapper,
                         Class<? extends Job> batchJobClass) {
            super(key, propsMapper);
            this.batchJobClass = batchJobClass;
        }

        public Batch(String key,
                         Function<JobDataMap, JobDataMap> propsMapper,
                         Predicate<JobDataMap> successfulJobCondition,
                         Class<? extends Job> batchJobClass) {
            super(key, propsMapper, successfulJobCondition);
            this.batchJobClass = batchJobClass;
        }

        public Batch(String key,
                         Function<JobDataMap, JobDataMap> propsMapper,
                         Predicate<JobDataMap> successfulJobCondition,
                         Consumer<JobDataMap> returnPropsWriter,
                         Class<? extends Job> batchJobClass) {
            super(key, propsMapper, successfulJobCondition, returnPropsWriter);
            this.batchJobClass = batchJobClass;
        }

        @Override
        public void triggerJob(JobExecutionContext context, JobDataMap props, Scheduler scheduler) throws SchedulerException {
            TaskUtils.triggerBatchJob(context, props, scheduler, getKey(), getPropsMapper(), batchJobClass);
        }
    }
}
