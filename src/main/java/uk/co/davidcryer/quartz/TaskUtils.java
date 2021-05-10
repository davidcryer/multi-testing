package uk.co.davidcryer.quartz;

import org.apache.tomcat.util.codec.binary.Base64;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskUtils {
    private static final String PROPS_IS_FINISHED = "isFinished";
    private static final String PROPS_IS_ERRORED = "isErrored";
    private static final String PROPS_ERROR = "error";
    private static final String PROPS_ERRORED_TASKS = "erroredTasks";

    public static void markAsErrored(JobExecutionContext context, String message) {
        markAsErrored(context.getJobDetail().getJobDataMap(), message);
    }

    public static void markAsErrored(JobDataMap props, String message) {
        props.put(PROPS_IS_ERRORED, true);
        props.put(PROPS_ERROR, message);
    }

    public static boolean isLastJobErrored(JobExecutionContext context) {
        return isErrored(context.getTrigger().getJobDataMap());
    }

    public static boolean isErrored(JobDataMap props) {
        return props.containsKey(PROPS_IS_ERRORED) && props.getBoolean(PROPS_IS_ERRORED);
    }

    public static String getLastJobError(JobExecutionContext context) {
        return context.getTrigger().getJobDataMap().getString(PROPS_ERROR);
    }

    public static void markAsFinished(JobExecutionContext context) {
        context.getJobDetail().getJobDataMap().put(PROPS_IS_FINISHED, true);
    }

    public static boolean isFinished(JobDataMap props) {
        return props.containsKey(PROPS_IS_FINISHED) && props.getBoolean(PROPS_IS_FINISHED);
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
