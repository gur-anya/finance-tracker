package org.anyatasks.notifier.dtos;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class Event <T>{
    private UUID eventId;
    private String eventType;
    private Instant eventTimestamp;
    private T eventData;
}
