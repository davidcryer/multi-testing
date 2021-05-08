package uk.co.davidcryer.quartz;

import org.quartz.JobDataMap;

import java.util.function.Function;

public class TaskUtils {

    public static Function<JobDataMap, JobDataMap> pass(String propKey, Function<String, JobDataMap> propsMapper) {
        return props -> propsMapper.apply(props.getString(propKey));
    }
}
