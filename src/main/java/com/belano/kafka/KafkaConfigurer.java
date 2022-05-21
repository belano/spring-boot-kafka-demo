package com.belano.kafka;

import java.util.Map;
import java.util.UUID;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KafkaConfigurer<V, P extends ExtendedKafkaProperties> {
    protected final P kafkaProperties;
    protected final ObjectMapper objectMapper;

    public KafkaTemplate<String, V> createKafkaTemplate(
            ProducerFactory<String, V> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    public ProducerFactory<String, V> createProducerFactory() {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
        DefaultKafkaProducerFactory<String, V> factory =
                new DefaultKafkaProducerFactory<>(producerProperties);

        JsonSerializer<V> valueSerializer = getJsonSerializer();
        factory.setValueSerializer(valueSerializer);

        if (kafkaProperties.isTransactional()) {
            factory.setTransactionIdPrefix(getTransactionIdPrefix());
        }
        factory.setMaxAge(kafkaProperties.getProducer().getMaxAge());

        return factory;
    }

    public ConsumerFactory<String, V> createConsumerFactory() {
        DefaultKafkaConsumerFactory<String, V> factory =
                new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
        JsonDeserializer<V> jsonDeserializer = getJsonDeserializer();
        jsonDeserializer.addTrustedPackages("*");

        factory.setValueDeserializer(new ErrorHandlingDeserializer<>(jsonDeserializer));

        return factory;
    }

    public ConcurrentKafkaListenerContainerFactory createListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory kafkaConsumerFactory,
            KafkaTemplate kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        factory.setAutoStartup(kafkaProperties.getListener().isAutoStartup());
        FixedBackOff backOff = new FixedBackOff();
        backOff.setMaxAttempts(kafkaProperties.getListener().getMaxFailures());
        factory.setCommonErrorHandler(
                new DefaultErrorHandler(
                        new CustomDeadLetterPublishingRecoverer(
                                kafkaTemplate,
                                kafkaProperties.getConsumer().getClientId(),
                                kafkaProperties.getConsumer().getGroupId()),
                        backOff));
        return factory;
    }

    private String getTransactionIdPrefix() {
        return UUID.randomUUID().toString();
    }

    private JsonDeserializer<V> getJsonDeserializer() {
        return new JsonDeserializer<>(objectMapper);
    }

    private JsonSerializer<V> getJsonSerializer() {
        return new JsonSerializer<>(objectMapper);
    }
}
