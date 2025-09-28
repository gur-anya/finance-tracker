package org.anyatasks.notifier.eventHandlers;

import org.anyatasks.notifier.dtos.Event;

public interface EventHandler<T> {
    void handle(Event<T> event);
    String getEventType();
}
