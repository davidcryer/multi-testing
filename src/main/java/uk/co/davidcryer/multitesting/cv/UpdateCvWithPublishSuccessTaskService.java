package uk.co.davidcryer.multitesting.cv;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateCvWithPublishSuccessTaskService {
    private final CvRepository repository;

    public void updateCv(String cvId, boolean didPublishToClient, boolean didPublishToKafka) {
        repository.update(cvId, didPublishToClient, didPublishToKafka);
    }
}
