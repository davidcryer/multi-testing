package uk.co.davidcryer.multitesting.potato;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PotatoMessageListener {
    private final PotatoService service;

    @KafkaListener(topics = PotatoMessage.TOPIC, containerFactory = "potatoMessageListenerFactory")
    public void listen(PotatoMessage message) {
        service.add(message);
    }
}
