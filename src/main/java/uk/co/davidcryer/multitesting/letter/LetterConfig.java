package uk.co.davidcryer.multitesting.letter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import uk.co.davidcryer.multitesting.config.KafkaConfig;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LetterConfig {
    private final KafkaConfig kafkaConfig;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LetterMessage> letterMessageListenerFactory() {
        return kafkaConfig.containerFactory(LetterMessage.class);
    }
}
