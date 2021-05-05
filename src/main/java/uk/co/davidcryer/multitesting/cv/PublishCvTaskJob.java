package uk.co.davidcryer.multitesting.cv;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.davidcryer.jobs.ConcurrentTasksJob;

import java.util.function.Function;

@Component
public class PublishCvTaskJob extends ConcurrentTasksJob {
    public static final String KEY = "publish-cv";

    @Autowired
    public PublishCvTaskJob(Scheduler scheduler) {
        super(scheduler, KEY, PublishCvToClientTaskJob.KEY, PublishCvToKafkaTaskJob.KEY);//TODO remove duplication of keys here and below
    }

    @Override
    protected void triggerConcurrentTasks(JobExecutionContext context, JobDataMap props) throws SchedulerException {
        var cvId = props.getString("cvId");
        triggerJob(context, PublishCvToClientTaskJob.KEY, PublishCvToClientTaskJob.props(cvId));
        triggerJob(context, PublishCvToKafkaTaskJob.KEY, PublishCvToKafkaTaskJob.props(cvId));
    }

    public static JobDataMap props(String cvId) {
        var props = new JobDataMap();
        props.put("cvId", cvId);
        return props;
    }

    @Override
    protected void writeToReturnProps(JobExecutionContext context, JobDataMap props) {
        props.put("cvId", context.getMergedJobDataMap().getString("cvId"));
    }

    public static JobDataMap mapReturnProps(JobDataMap props, Function<String, JobDataMap> function) {
        var cvId = props.getString("cvId");
        return function.apply(cvId);
    }
}
