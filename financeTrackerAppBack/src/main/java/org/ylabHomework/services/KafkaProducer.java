package org.ylabHomework.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anyaTasks.DTOs.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;



@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Event<?>> kafkaTemplate;

    public void publish(String topic, String key, Event<?> event) {
        kafkaTemplate.send(topic, key, event);
        log.info("KAFKA PRODUCER: sent message: {}", event);
    }
}