package com.belano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.belano.model.SomeEvent;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableTransactionManagement
@Slf4j
public class SpringBootKafkaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootKafkaDemoApplication.class, args);
    }

    @KafkaListener(
            topics = "${some-events.kafka.topic.name}",
            containerFactory = "someEventsContainerFactory",
            groupId = "DEMO-app")
    public void listen(SomeEvent event) {
        log.info(String.format("Some Event Kafka Message Consumed {id:%s}", event.getId()));
    }
}
