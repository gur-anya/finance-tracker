package org.anyaTasks.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event <T>{
    @JsonProperty("eventId")
    private UUID eventId;
    @JsonProperty("eventType")
    private String eventType;
    @JsonProperty("eventTimestamp")
    private Instant eventTimestamp;
    @JsonProperty("eventData")
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "eventType"
    )
    @JsonSubTypes({
        @JsonSubTypes.Type(value = UserRegisteredEvent.class, name = "user.successful-registration")
    })
    private T eventData;

    public Event(UUID eventId, String eventType, Instant eventTimestamp) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventTimestamp = eventTimestamp;
    }
}
