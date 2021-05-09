package uk.co.davidcryer.quartz;

import org.quartz.JobDataMap;

import java.util.function.Function;

public class TaskUtils {
    private static final String PROPS_IS_ERRORED = "isErrored";
    private static final String PROPS_IS_FINISHED = "isFinished";

    static void markAsErrored(JobDataMap props) {
        props.put(PROPS_IS_ERRORED, true);
    }

    static boolean isErrored(JobDataMap props) {
        return props.containsKey(PROPS_IS_ERRORED) && props.getBoolean(PROPS_IS_ERRORED);
    }

    static void markAsFinished(JobDataMap props) {
        props.put(PROPS_IS_FINISHED, true);
    }

    static boolean isFinished(JobDataMap props) {
        return props.containsKey(PROPS_IS_FINISHED) && props.getBoolean(PROPS_IS_FINISHED);
    }

    public static Function<JobDataMap, JobDataMap> pass(String propKey, Function<String, JobDataMap> propsMapper) {
        return props -> propsMapper.apply(props.getString(propKey));
    }
}
