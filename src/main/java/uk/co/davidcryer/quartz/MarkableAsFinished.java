package uk.co.davidcryer.quartz;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public interface MarkableAsFinished {
    String PROPS_IS_FINISHING = "isFinishing";

    default void markAsFinished(JobExecutionContext context, JobDataMap props) {
        context.getJobDetail().getJobDataMap().put(PROPS_IS_FINISHING, true);
    }

    static boolean isFinished(JobDataMap props) {
        return props.containsKey(PROPS_IS_FINISHING) && props.getBoolean(PROPS_IS_FINISHING);
    }
}
