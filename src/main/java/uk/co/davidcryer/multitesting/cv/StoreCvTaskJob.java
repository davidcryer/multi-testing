package uk.co.davidcryer.multitesting.cv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.quartz.TaskJob;

import java.util.function.Function;

@Component
public class StoreCvTaskJob extends TaskJob {
    public static final String KEY = "store-cv";
    private final StoreCvTaskService service;
    private final ObjectMapper objectMapper;
    private String cvId;

    @Autowired
    public StoreCvTaskJob(Scheduler scheduler, StoreCvTaskService service, ObjectMapper objectMapper) {
        super(scheduler);
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void executeTask(JobExecutionContext context) throws JobExecutionException {
        try {
            var props = context.getMergedJobDataMap();
            var request = objectMapper.readValue(props.getString("cv"), CvRequest.class);
            cvId = service.add(request);
        } catch (JsonProcessingException e) {
            throw new JobExecutionException(e);
        }
    }

    @Override
    public void writeToReturnProps(JobExecutionContext context, JobDataMap props) {
        props.put("cvId", cvId);
    }

    public static JobDataMap props(String cv) {
        var props = new JobDataMap();
        props.put("cv", cv);
        return props;
    }

    public static JobDataMap mapReturnProps(JobDataMap props, Function<String, JobDataMap> map) {
        var cvId = props.getString("cvId");
        return map.apply(cvId);
    }
}
