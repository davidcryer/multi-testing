package uk.co.davidcryer.multitesting.cv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CvService {
    private final Scheduler scheduler;
    private final ObjectMapper objectMapper;

    public boolean add(CvRequest cv) {
        try {
            var request = objectMapper.writeValueAsString(cv);
            var props = SaveCvOrchestratorJob.props(request);
            var jobId = UUID.randomUUID().toString();
            scheduler.addJob(JobBuilder
                    .newJob(SaveCvOrchestratorJob.class)
                    .withIdentity(jobId)
                    .storeDurably()
                    .build(), true);
            scheduler.triggerJob(JobKey.jobKey(jobId), props);
            return true;
        } catch (SchedulerException | JsonProcessingException e) {
            return false;
        }
    }
}
