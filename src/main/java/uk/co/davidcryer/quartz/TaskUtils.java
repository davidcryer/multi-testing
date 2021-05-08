package uk.co.davidcryer.quartz;

import org.quartz.JobDataMap;

import java.util.function.Function;

public class TaskUtils {

    public static Function<JobDataMap, JobDataMap> mapProps(Function<String, JobDataMap> propsMapper, String propKey) {
        return props -> propsMapper.apply(props.getString(propKey));
    }
}
