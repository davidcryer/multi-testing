package uk.co.davidcryer.multitesting.cv;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Cv;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PublishCvTaskService {
    private final CvRepository cvRepository;
    private final Producer<String, Object> kafkaProducer;

    public void add(String cvId) {
        cvRepository.get(cvId).map(this::toMessage).ifPresent(this::publish);
    }

    private CvMessage toMessage(Cv cv) {
        return new CvMessage(
                cv.getId(),
                cv.getCreated().atZone(ZoneId.of("UTC")),
                cv.getEmailaddress(),
                cv.getPhonenumber(),
                cv.getName(),
                cv.getContent()
        );
    }

    private void publish(CvMessage cv) {
        kafkaProducer.send(new ProducerRecord<>(CvMessage.TOPIC, cv.getId(), cv));
    }
}
