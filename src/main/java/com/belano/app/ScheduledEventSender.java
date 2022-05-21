package com.belano.app;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.belano.model.SomeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledEventSender {
    private final EventSender eventSender;

    @Scheduled(fixedRate = 5000)
    public void sendEvent() {
        SomeEvent event = new SomeEvent();
        log.info(
                String.format(
                        "Some Event Scheduled Kafka Message Produced {id:%s}", event.getId()));
        eventSender.sendEvent(event);
    }
}
