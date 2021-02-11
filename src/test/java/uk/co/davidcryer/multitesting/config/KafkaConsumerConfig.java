package uk.co.davidcryer.multitesting.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import uk.co.davidcryer.multitesting.complex.ComplexTopic;
import uk.co.davidcryer.multitesting.utils.KafkaHelper;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("integration")
public class KafkaConsumerConfig {

    private final String kafkaServer;
    private final String schemaRegistry;

    public KafkaConsumerConfig(@Value("${kafka.server}") String kafkaServer, @Value("${schema.registry}") String schemaRegistry) {
        this.kafkaServer = kafkaServer;
        this.schemaRegistry = schemaRegistry;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> properties = getProperties();
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    private Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group-" + hashCode());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DefaultSerializer.class);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 110000);
        properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 100000);

        return properties;
    }

    @Bean
    public KafkaHelper kafkaHelper(ConsumerFactory<String, Object> consumerFactory) {
        return new KafkaHelper(consumerFactory).startConsumingAvroTopics(ComplexTopic.NAME);
    }
}
