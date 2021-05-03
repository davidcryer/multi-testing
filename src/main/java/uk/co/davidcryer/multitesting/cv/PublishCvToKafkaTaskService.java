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

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PublishCvToKafkaTaskService {
    private final CvRepository cvRepository;
    private final Producer<String, Object> kafkaProducer;

    public boolean add(String cvId) {
        return cvRepository.get(cvId)
                .map(this::toMessage)
                .map(this::publish)
                .orElse(false);
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

