package uk.co.davidcryer.multitesting.cv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static uk.co.davidcryer.multitesting.cv.SaveCvOrchestratorJob.buildProps;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CvService {
    private final Scheduler scheduler;
    private final ObjectMapper objectMapper;

    public boolean add(CvRequest cv) {
        try {
            var request = objectMapper.writeValueAsString(cv);
            scheduler.triggerJob(JobKey.jobKey(SaveCvOrchestratorJob.KEY), buildProps(request));
            return true;
        } catch (SchedulerException | JsonProcessingException e) {
            return false;
        }
    }
}
