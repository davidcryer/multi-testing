package uk.co.davidcryer.quartz;

import org.quartz.JobDataMap;

import java.util.function.Function;
import java.util.function.Supplier;

public class PropsUtils {

    public static Supplier<JobDataMap> pass(JobDataMap props, String propKey, Function<String, JobDataMap> propsMapper) {
        return () -> propsMapper.apply(props.getString(propKey));
    }
}
