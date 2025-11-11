package org.anyatasks.notifier.services;

import lombok.extern.slf4j.Slf4j;
import org.anyaTasks.DTOs.Event;
import org.anyatasks.notifier.eventHandlers.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@SuppressWarnings("rawtypes")
public class EventDispatchingService {
    private final Map<String, EventHandler> handlers;

    @Autowired
    public EventDispatchingService(List<EventHandler> eventHandlers) {
        this.handlers = eventHandlers.stream()
            .collect(Collectors.toMap(
                EventHandler::getEventType,
                handler -> handler
            ));
    }

    @SuppressWarnings("unchecked")
    public void dispatch(Event<?> event) {
        String eventType = event.getEventType();
        EventHandler handler = handlers.get(eventType);

        if (handler != null) {
            handler.handle(event);
        } else {
            log.warn("No handler found for event type: {}", eventType);
        }
    }
}
