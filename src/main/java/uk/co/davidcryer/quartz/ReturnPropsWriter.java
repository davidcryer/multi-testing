package uk.co.davidcryer.quartz;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.function.BiConsumer;

import static uk.co.davidcryer.quartz.TaskUtils.markAsErrored;

public interface ReturnPropsWriter {
    default void writeToReturnProps(JobExecutionContext context, JobDataMap returnProps) {}

    static BiConsumer<JobExecutionContext, JobDataMap> getErrorWriterForReturnProps(Throwable t) {
        return (context, props) -> markAsErrored(props, t.getMessage());
    }
}
