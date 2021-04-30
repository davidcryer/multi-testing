package uk.co.davidcryer.multitesting.cv;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Cv;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreCvTaskService {
    private final CvRepository cvRepository;

    public String add(CvRequest request) {
        var cv = new Cv(
                UUID.randomUUID().toString(),
                OffsetDateTime.now(ZoneId.of("UTC")),
                request.getEmailAddress(),
                request.getPhoneNumber(),
                request.getName(),
                request.getContent()
        );
        return cvRepository.add(cv).getId();
    }
}
