package org.anyatasks.notifier.eventHandlers;


import org.anyaTasks.DTOs.Event;

public interface EventHandler<T> {
    void handle(Event<T> event);
    String getEventType();
}
