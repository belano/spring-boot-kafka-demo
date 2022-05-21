package com.belano.kafka;

import java.time.Duration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtendedKafkaProperties extends KafkaProperties {

    private final Topic topic = new Topic();
    private final Listener listener = new Listener();
    private final Producer producer = new Producer();
    private boolean transactional = true;
    private boolean scheduled = false;

    @Getter
    @Setter
    public static class Topic {
        private String name;
        private String dlt;
    }

    @Getter
    @Setter
    public static class Listener extends KafkaProperties.Listener {
        boolean autoStartup = true;
        int maxFailures = 0;
    }

    @Getter
    @Setter
    public static class Producer extends KafkaProperties.Producer {
        Duration maxAge = Duration.ofDays(1);
        String maxSize;
    }
}
