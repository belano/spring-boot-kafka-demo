package com.belano.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("some-events.kafka")
public class SomeEventsKafkaProperties extends ExtendedKafkaProperties {}
