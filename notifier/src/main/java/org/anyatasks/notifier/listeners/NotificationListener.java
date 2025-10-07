package org.anyatasks.notifier.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anyaTasks.DTOs.Event;
import org.anyaTasks.DTOs.UserRegisteredEvent;
import org.anyatasks.notifier.services.EventDispatchingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final EventDispatchingService eventDispatchingService;

    @KafkaListener(topics = "user.successful-registration", groupId = "notification-service-group")
    public void listen(Event<UserRegisteredEvent> event) {
        log.info("SUCCESSFULLY DESERIALIZED EVENT: {}", event);

        UserRegisteredEvent userData = event.getEventData();
        log.info("Processing registration for user: {}", userData.getUsername());

        eventDispatchingService.dispatch(event);
    }
}

