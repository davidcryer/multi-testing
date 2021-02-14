package uk.co.davidcryer.multitesting.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import uk.co.davidcryer.multitesting.letter.LetterMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    private final String kafkaServer;

    @Autowired
    public KafkaConfig(@Value("${kafka.server}") String kafkaServer) {
        this.kafkaServer = kafkaServer;
    }

    @Bean
    public ConsumerFactory<String, LetterMessage> consumerFactory() {
        var properties = new HashMap<String, Object>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "multi-testing");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 110000);
        properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 100000);
        return new DefaultKafkaConsumerFactory<>(
                properties,
                new StringDeserializer(),
                new JsonDeserializer<>(LetterMessage.class, false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LetterMessage> letterMessageListenerFactory(
            ConsumerFactory<String, LetterMessage> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, LetterMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public Producer<String, Object> kafkaProducer() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        return new KafkaProducer<>(properties, new StringSerializer(), new JsonSerializer<>());
    }
}
