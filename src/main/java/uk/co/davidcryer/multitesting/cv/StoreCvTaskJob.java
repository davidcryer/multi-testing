package uk.co.davidcryer.multitesting.cv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.multitesting.job.TaskJob;

@Component
public class StoreCvTaskJob extends TaskJob {
    public static final String KEY = "store-cv";
    private final StoreCvTaskService service;
    private final ObjectMapper objectMapper;

    @Autowired
    public StoreCvTaskJob(Scheduler scheduler, StoreCvTaskService service, ObjectMapper objectMapper) {
        super(scheduler, KEY);
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            var props = context.getMergedJobDataMap();
            var request = objectMapper.readValue(props.getString("cv"), CvRequest.class);
            var cvId = service.add(request);
            endTask(context, nextProps -> {
                nextProps.put("cvId", cvId);
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static JobDataMap buildProps(String cv) {
        var props = new JobDataMap();
        props.put("cv", cv);
        return props;
    }

    public static <T> T withProps(JobDataMap props, PropsConsumer<T> consumer) {
        var cvId = props.getString("cvId");
        return consumer.apply(cvId);
    }

    interface PropsConsumer<T> {
        T apply(String cvId);
    }
}
