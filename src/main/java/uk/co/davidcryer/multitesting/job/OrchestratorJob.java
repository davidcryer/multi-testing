package uk.co.davidcryer.multitesting.job;

import org.quartz.Job;
import org.quartz.JobDataMap;

public abstract class OrchestratorJob implements Job {

    protected String getLastJob(JobDataMap props) {
        var lastJob = "";
        if (props.containsKey("orchestrator.lastJob")) {
            lastJob = props.getString("orchestrator.lastJob");
        }
        return lastJob;
    }

    public static JobDataMap buildProps(String lastJob) {
        var props = new JobDataMap();
        props.put("orchestrator.lastJob", lastJob);
        return props;
    }
}
