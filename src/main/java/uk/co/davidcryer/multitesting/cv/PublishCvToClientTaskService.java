package uk.co.davidcryer.multitesting.cv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Cv;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class PublishCvToClientTaskService {
    private final CvRepository repository;
    private final CvClient client;

    public boolean add(String cvId) {
        return repository.get(cvId)
                .map(this::toClientRequest)
                .map(client::post)
                .orElse(false);
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
}
