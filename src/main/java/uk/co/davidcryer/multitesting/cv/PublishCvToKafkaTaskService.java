package uk.co.davidcryer.multitesting.cv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Cv;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PublishCvToKafkaTaskService {
    private final CvRepository repository;
    private final Producer<String, Object> kafkaProducer;

    public void add(String cvId) {
        repository.get(cvId)
                .map(this::toMessage)
                .map(this::publish)
                .ifPresent(updateCvWithPublishStatus(cvId));
    }

    private CvMessage toMessage(Cv cv) {
        return new CvMessage(
                cv.getId(),
                cv.getCreated(),
                cv.getName(),
                cv.getEmailAddress(),
                cv.getPhoneNumber(),
                cv.getContent()
        );
    }

    private boolean publish(CvMessage cv) {
        kafkaProducer.send(new ProducerRecord<>(CvMessage.TOPIC, cv.getId(), cv));
        return true;
    }

    private Consumer<Boolean> updateCvWithPublishStatus(String cvId) {
        return didPublish -> repository.update(cvId, cv -> cv.setIsPublishedToKafka(didPublish));
    }
}

@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@Builder
class CvMessage {
    public static final String TOPIC = "cv";
    private String id;
    private LocalDateTime created;
    private String name;
    private String emailAddress;
    private String phoneNumber;
    private String content;
}

