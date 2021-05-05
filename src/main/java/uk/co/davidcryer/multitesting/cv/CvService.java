package uk.co.davidcryer.multitesting.cv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CvService {
    private final Scheduler scheduler;
    private final ObjectMapper objectMapper;

    public boolean add(CvRequest cv) {
        try {
            var request = objectMapper.writeValueAsString(cv);
            var props = SaveCvOrchestratorJob.props(request);
            scheduler.triggerJob(JobKey.jobKey(SaveCvOrchestratorJob.KEY), props);
            return true;
        } catch (SchedulerException | JsonProcessingException e) {
            return false;
        }
    }
}
