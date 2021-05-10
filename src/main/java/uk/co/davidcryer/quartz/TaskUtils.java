package uk.co.davidcryer.quartz;

import org.apache.tomcat.util.codec.binary.Base64;
import org.quartz.JobDataMap;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TaskUtils {
    private static final String PROPS_IS_FINISHED = "isFinished";
    private static final String PROPS_IS_ERRORED = "isErrored";
    private static final String PROPS_ERROR = "error";
    private static final String PROPS_ERRORED_TASKS = "erroredTasks";

    static void markAsErrored(JobDataMap props, String message) {
        props.put(PROPS_IS_ERRORED, true);
        props.put(PROPS_ERROR, message);
    }

    static boolean isErrored(JobDataMap props) {
        return props.containsKey(PROPS_IS_ERRORED) && props.getBoolean(PROPS_IS_ERRORED);
    }

    static String getError(JobDataMap props) {
        return props.getString(PROPS_ERROR);
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

    public static void addErroredTaskEntry(JobDataMap props, Task task, String error) {
        var erroredTasks = props.containsKey(PROPS_ERRORED_TASKS) ? props.getString(PROPS_ERRORED_TASKS) : null;
        var errorEntry = task.getKey() + ": " + error;
        var encodedEntry = new String(Base64.encodeBase64(errorEntry.getBytes(StandardCharsets.UTF_8)));
        if (erroredTasks == null) {
            erroredTasks = encodedEntry;
        } else {
            erroredTasks = erroredTasks.concat("," + encodedEntry);
        }
        props.put("erroredTasks", erroredTasks);
    }

    public static boolean hasErroredTasks(JobDataMap props) {
        return props.containsKey(PROPS_ERRORED_TASKS);
    }

    public static List<String> getErroredTaskEntries(JobDataMap props) {
        var erroredTasks = props.containsKey(PROPS_ERRORED_TASKS) ? props.getString(PROPS_ERRORED_TASKS) : null;
        if (erroredTasks == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(erroredTasks.split(","))
                .map(Base64::decodeBase64)
                .map(String::new)
                .collect(Collectors.toList());
    }
}
