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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CvService {
    private final Scheduler scheduler;
    private final ObjectMapper objectMapper;

    @Transactional
    public boolean add(CvRequest cv) {
        try {
            var request = objectMapper.writeValueAsString(cv);
            var jobKey = JobKey.jobKey(SaveCvOrchestratorJob.KEY, cv.getEmailAddress());
            scheduler.addJob(JobBuilder.newJob(SaveCvOrchestratorJob.class)
                    .withIdentity(jobKey)
                    .storeDurably()
                    .build(),
                    false);
            scheduler.triggerJob(jobKey, SaveCvOrchestratorJob.props(request));
            return true;
        } catch (SchedulerException e) {
            return false;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
