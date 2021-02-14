package uk.co.davidcryer.multitesting.letter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LetterMessageListener {
    private final LetterService service;

    @KafkaListener(topics = LetterMessage.TOPIC, containerFactory = "letterMessageListenerFactory")
    public void listen(LetterMessage letterMessage) {
        service.add(letterMessage);
    }
}
