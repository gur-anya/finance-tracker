package org.anyatasks.notifier.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
public class KafkaDLQConfig {
    @Value("${spring.kafka.dlq.topic}")
    private String deadLetterTopic;

    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            kafkaTemplate,
            (consumerRecord, exception) -> new org.apache.kafka.common.TopicPartition(deadLetterTopic, consumerRecord.partition())
        );
        FixedBackOff backOff = new FixedBackOff(1000L, 2);
        return new DefaultErrorHandler(recoverer, backOff);
    }
}
