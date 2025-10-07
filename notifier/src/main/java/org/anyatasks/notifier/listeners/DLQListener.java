package org.anyatasks.notifier.listeners;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DLQListener {

    @KafkaListener(id = "dlq-listener", topics = "${spring.kafka.dlq.topic}")
    public void handleDlqMessage(ConsumerRecord<String, Object> message,
                                 @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String exceptionMessage,
                                 @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic,
                                 @Header(KafkaHeaders.DLT_EXCEPTION_STACKTRACE) String stacktrace) {

        log.error("KAFKALISTENER: Failed to process message from topic {}; exception message: {} message key: {} message value: {} stacktrace: {} ",
            originalTopic, exceptionMessage, message.key(), message.value(), stacktrace);
    }
}