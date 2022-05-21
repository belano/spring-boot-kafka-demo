package com.belano.kafka;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.lang.Nullable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomDeadLetterPublishingRecoverer extends DeadLetterPublishingRecoverer {

    private final String clientId;
    private final String groupId;

    public CustomDeadLetterPublishingRecoverer(
            KafkaTemplate<Object, Object> template, String clientId, String groupId) {
        super(
                template,
                (cr, e) -> {
                    log.error("Failed to process message", e);
                    return new TopicPartition(cr.topic() + "_DLT", cr.partition());
                });
        this.clientId = clientId;
        this.groupId = groupId;
    }

    @Override
    protected ProducerRecord<Object, Object> createProducerRecord(
            ConsumerRecord<?, ?> consumerRecord,
            TopicPartition topicPartition,
            Headers headers,
            @Nullable byte[] key,
            @Nullable byte[] value) {

        headers.add(
                new RecordHeader(
                        "kafka_dlt-exception-timestamp",
                        ByteBuffer.allocate(8).putLong(Instant.now().toEpochMilli()).array()));
        headers.add(
                new RecordHeader(
                        "kafka_dlt-exception-timestamp-type",
                        consumerRecord.timestampType().toString().getBytes(StandardCharsets.UTF_8)));
        headers.add(
                new RecordHeader(
                        "kafka_dlt-original-client-id", clientId.getBytes(StandardCharsets.UTF_8)));
        headers.add(
                new RecordHeader(
                        "kafka_dlt-original-group-id", groupId.getBytes(StandardCharsets.UTF_8)));
        return super.createProducerRecord(consumerRecord, topicPartition, headers, key, value);
    }
}
