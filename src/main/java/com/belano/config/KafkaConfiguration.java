package com.belano.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaAwareTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import com.belano.kafka.KafkaConfigurer;
import com.belano.kafka.SomeEventsKafkaProperties;
import com.belano.model.SomeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;

@Configuration
@EnableConfigurationProperties(SomeEventsKafkaProperties.class)
public class KafkaConfiguration {

    private final SomeEventsKafkaProperties someEventsKafkaProperties;

    @Getter(value = AccessLevel.PROTECTED)
    private final KafkaConfigurer<SomeEvent, SomeEventsKafkaProperties> configurer;

    public KafkaConfiguration(SomeEventsKafkaProperties properties, ObjectMapper objectMapper) {
        someEventsKafkaProperties = properties;
        configurer = new KafkaConfigurer<>(properties, objectMapper);
    }

    @Bean("someEventsKafkaTemplate")
    public KafkaTemplate<String, SomeEvent> someEventsKafkaTemplate() {
        return getConfigurer().createKafkaTemplate(someEventsProducerFactory());
    }

    @Bean
    public ProducerFactory<String, SomeEvent> someEventsProducerFactory() {
        return getConfigurer().createProducerFactory();
    }

    @Bean("someEventsContainerFactory")
    public KafkaListenerContainerFactory portfolioEventsContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            @Qualifier("someEventsConsumerFactory") ConsumerFactory someEventsConsumerFactory,
            @Qualifier("someEventsKafkaTemplate") KafkaTemplate someEventsKafkaTemplate) {
        return getConfigurer()
                .createListenerContainerFactory(
                        configurer, someEventsConsumerFactory, someEventsKafkaTemplate);
    }

    @Primary
    @Bean("someEventsConsumerFactory")
    public ConsumerFactory<String, SomeEvent> portfolioEventsConsumerFactory() {
        return getConfigurer().createConsumerFactory();
    }

    @Bean("portfolioEventsConsumer")
    public Consumer<String, SomeEvent> portfolioEventsConsumer(
            @Qualifier("someEventsConsumerFactory")
                    ConsumerFactory<String, SomeEvent> portfolioEventsConsumerFactory) {
        return portfolioEventsConsumerFactory.createConsumer("DEMO", "DEMO");
    }

    @Bean
    @Primary
    public SomeEventsKafkaProperties someEventsKafkaProperties() {
        return someEventsKafkaProperties;
    }

    @Bean
    public KafkaAwareTransactionManager<String, SomeEvent> tm() {
        return new KafkaTransactionManager<>(someEventsProducerFactory());
    }
}
