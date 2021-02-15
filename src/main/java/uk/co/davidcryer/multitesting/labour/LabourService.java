package uk.co.davidcryer.multitesting.labour;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LabourService {
    private final Producer<String, Object> kafkaProducer;

    public void add(FruitRequest request) {
        var fruit = new FruitMessage(
                UUID.randomUUID().toString(),
                ZonedDateTime.now(),
                request.getDescription()
        );
        kafkaProducer.send(new ProducerRecord<>(FruitMessage.TOPIC, fruit.getId(), fruit));
    }
}
