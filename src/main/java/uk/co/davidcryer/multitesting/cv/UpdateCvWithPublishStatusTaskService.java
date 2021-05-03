package uk.co.davidcryer.multitesting.cv;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateCvWithPublishStatusTaskService {
    private final CvRepository repository;

    public void updateCv(String cvId) {
        repository.update(cvId, cv -> {
            var didPublishToClient = cv.getIsPublishedToClient();
            var didPublishToKafka = cv.getIsPublishedToKafka();
            cv.setIsFullyPublished(didPublishToClient && didPublishToKafka);
        });
    }
}
