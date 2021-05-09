package uk.co.davidcryer.quartz;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.function.BiConsumer;

public interface ReturnPropsWriter {
    default void writeToReturnProps(JobExecutionContext context, JobDataMap returnProps) {}

    static BiConsumer<JobExecutionContext, JobDataMap> getErrorWriterForReturnProps(Throwable t) {
        return (context, jobDataMap) -> {
            jobDataMap.put("didError", true);
            jobDataMap.put("error", t.getMessage());
        };
    }
}
