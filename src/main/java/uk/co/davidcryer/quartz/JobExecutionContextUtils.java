package uk.co.davidcryer.quartz;

import org.quartz.JobExecutionContext;

public class JobExecutionContextUtils {

    public static String getJobName(JobExecutionContext context) {
        return context.getJobDetail().getKey().getName();
    }
}
