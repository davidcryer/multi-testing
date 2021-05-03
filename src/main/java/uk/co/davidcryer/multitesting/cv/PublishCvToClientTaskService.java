package uk.co.davidcryer.multitesting.cv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Cv;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class PublishCvToClientTaskService {
    private final CvRepository repository;
    private final CvClient client;

    public void add(String cvId) {
        repository.get(cvId)
                .map(this::toClientRequest)
                .map(client::post)
                .ifPresent(updateCvWithPublishStatus(cvId));
    }

    private CvClientRequest toClientRequest(Cv cv) {
        return new CvClientRequest(
                cv.getId(),
                cv.getCreated(),
                cv.getName(),
                cv.getEmailAddress(),
                cv.getPhoneNumber(),
                cv.getContent()
        );
    }

    private Consumer<Boolean> updateCvWithPublishStatus(String cvId) {
        return didPublish -> repository.update(cvId, cv -> cv.setIsPublishedToClient(didPublish));
    }
}
