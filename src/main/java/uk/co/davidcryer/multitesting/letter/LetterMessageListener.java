package uk.co.davidcryer.multitesting.letter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LetterMessageListener {
    private final LetterService service;

    @KafkaListener(topics = LetterMessage.TOPIC, containerFactory = "letterMessageListenerFactory")
    public void listen(LetterMessage message) {
        service.add(message);
    }
}
