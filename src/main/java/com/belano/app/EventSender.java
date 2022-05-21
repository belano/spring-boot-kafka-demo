package com.belano.app;

import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.belano.model.SomeEvent;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventSender {

    private final KafkaTemplate<String, SomeEvent> someEventsKafkaTemplate;
    private final String someEventsTopicName;

    public EventSender(
            KafkaTemplate<String, SomeEvent> someEventsKafkaTemplate,
            @Value("${some-events.kafka.topic.name}") String someEventsTopicName) {
        this.someEventsKafkaTemplate = someEventsKafkaTemplate;
        this.someEventsTopicName = someEventsTopicName;
    }

    @Transactional
    public void sendEvent(SomeEvent event) {
        try {
            long begin = System.currentTimeMillis();
            // Using the call to get() to make it a blocking operation
            someEventsKafkaTemplate.send(someEventsTopicName, event.getId(), event).get();
            log.info(
                    String.format(
                            "Some Event Kafka Message Produced {id:%s} in [%s]ms",
                            event.getId(), System.currentTimeMillis() - begin));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new EventSenderException(e);
        }
    }
}
