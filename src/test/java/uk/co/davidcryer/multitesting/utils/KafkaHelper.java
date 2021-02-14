package uk.co.davidcryer.multitesting.utils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;

import java.io.Closeable;
import java.util.*;
import java.util.stream.Collectors;

public class KafkaHelper implements Closeable {
    private final ConsumerFactory<String, Object> consumerFactory;

    private Map<String, List<Object>> consumedMessages = new HashMap<>();
    private KafkaMessageListenerContainer<String, Object> avroContainer;

    public KafkaHelper(String kafkaServer) {
        var properties = new HashMap<String, Object>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "multi-testing-" + hashCode());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 110000);
        properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 100000);
        this.consumerFactory = new DefaultKafkaConsumerFactory<>(properties);
    }

    public KafkaHelper startConsumingAvroTopics(String... topics) {
        close();
        initialiseTopics(topics);
        ContainerProperties containerProperties = new ContainerProperties(topics);
        avroContainer = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        avroContainer.setupMessageListener((MessageListener<String, Object>) record ->
                consumedMessages.get(record.topic()).add(record.value())
        );
        avroContainer.start();
        return this;
    }

    private void initialiseTopics(String[] topics) {
        consumedMessages = new HashMap<>();
        Arrays.stream(topics).forEach(topic -> consumedMessages.put(topic, new ArrayList<>()));
    }

    @Override
    public void close() {
        if (avroContainer != null) {
            avroContainer.stop();
        }
    }

    public <T> List<T> getConsumedMessages(String topic, Class<T> modelClass) {
        return getRecordsForClass(topic, modelClass);
    }

    public <T> List<T> getAndClearConsumedMessages(String topicName, Class<T> modelClass, int expectedNumMessages, long timeoutMillis)
            throws TimeoutException, InterruptedException {
        List<T> consumedEntityList = getConsumedMessages(topicName, modelClass);

        long timeoutTime = System.currentTimeMillis() + timeoutMillis;

        try {
            while (consumedEntityList.size() < expectedNumMessages) {
                consumedEntityList = getConsumedMessages(topicName, modelClass);
                if (System.currentTimeMillis() > timeoutTime) {
                    throw new TimeoutException("Only consumed " + consumedEntityList.size() +
                            " of the expected " + expectedNumMessages + " " + modelClass.getSimpleName() + " messages within timeout limit: " + consumedEntityList);
                }
                Thread.sleep(50);
            }
            if (consumedEntityList.size() > expectedNumMessages) {
                throw new AssertionError("Consumed " + consumedEntityList.size() +
                        " " + modelClass.getSimpleName() + " messages, but expected only " + expectedNumMessages + ": " + consumedEntityList);
            }

            return consumedEntityList;

        } finally {
            clearConsumedMessages(topicName);
        }
    }

    public void clearConsumedMessages(String... topicNames) {
        for (String topicName : topicNames) {
            consumedMessages.get(topicName).clear();
        }
    }

    public void clearAllConsumedMessages() {
        for (List<Object> messages : consumedMessages.values()) {
            messages.clear();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getRecordsForClass(String topic, Class<T> modelClass) {
        return consumedMessages.get(topic)
                .stream()
                .map(record -> (T) record)
                .collect(Collectors.toList());
    }
}
