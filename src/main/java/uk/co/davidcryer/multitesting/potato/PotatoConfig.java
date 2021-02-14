package uk.co.davidcryer.multitesting.potato;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.web.client.RestTemplate;
import uk.co.davidcryer.multitesting.config.KafkaConfig;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PotatoConfig {
    private final KafkaConfig kafkaConfig;

    @Bean
    public RestTemplate potatoTemplate() {
        return new RestTemplateBuilder()
                .rootUri("http://localhost:9876")
                .build();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PotatoMessage> potatoMessageListenerFactory() {
        return kafkaConfig.containerFactory(PotatoMessage.class);
    }
}
