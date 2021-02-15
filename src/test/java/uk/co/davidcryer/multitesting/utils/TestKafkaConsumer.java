package uk.co.davidcryer.multitesting.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TestKafkaConsumer<V> implements Closeable {
    private final KafkaMessageListenerContainer<String, V> container;
    private final List<V> messages = new ArrayList<>();

    public TestKafkaConsumer(KafkaMessageListenerContainer<String, V> container) {
        this.container = container;
        container.setupMessageListener((MessageListener<String, V>) record ->
                messages.add(record.value())
        );
        container.start();
    }

    @Override
    public void close() {
        container.stop();
    }

    public void clear() {
        messages.clear();
    }

    public List<V> get(int expectedNumMessages, long timeoutMillis) throws TimeoutException, InterruptedException {
        var timeoutTime = System.currentTimeMillis() + timeoutMillis;

        while (messages.size() < expectedNumMessages) {
            if (System.currentTimeMillis() > timeoutTime) {
                throw new TimeoutException("Only consumed " + messages.size() +
                        " of the expected " + expectedNumMessages + " messages within timeout limit: " + messages);
            }
            Thread.sleep(50);
        }
        if (messages.size() > expectedNumMessages) {
            throw new AssertionError("Consumed " + messages.size() + " messages, but expected only " + expectedNumMessages + ": " + messages);
        }

        return new ArrayList<>(messages);
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Builder<V> {
        private String kafkaServer;
        private String groupId;
        private String topic;
        private Class<V> topicClass;

        public Builder<V> kafkaServer(String kafkaServer) {
            this.kafkaServer = kafkaServer;
            return this;
        }

        public Builder<V> groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder<V> topic(String topic) {
            this.topic = topic;
            return this;
        }

        public <T> Builder<T> topicClass(Class<T> topicClass) {
            return new Builder<>(kafkaServer, groupId, topic, topicClass);
        }

        public TestKafkaConsumer<V> build() {
            Objects.requireNonNull(kafkaServer);
            Objects.requireNonNull(groupId);
            Objects.requireNonNull(topic);
            Objects.requireNonNull(topicClass);
            var properties = new HashMap<String, Object>();
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
            properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 110000);
            properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 100000);
            var consumerFactory = new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), getDeserializer());
            var containerProperties = new ContainerProperties(topic);
            var container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
            return new TestKafkaConsumer<>(container);
        }

        private Deserializer<V> getDeserializer() {
            if (topicClass == String.class) {
                //noinspection unchecked
                return (Deserializer<V>) new StringDeserializer();
            }
            return new JsonDeserializer<>(topicClass, false);
        }
    }
}
