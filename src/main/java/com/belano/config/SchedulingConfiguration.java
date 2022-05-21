package com.belano.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(
        name = "some-events.kafka.producer.scheduled",
        havingValue = "true",
        matchIfMissing = true)
public class SchedulingConfiguration {}
