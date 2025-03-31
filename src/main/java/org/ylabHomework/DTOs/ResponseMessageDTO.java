package org.ylabHomework.DTOs;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotEmpty;


@JsonPropertyOrder({"message"})
public class ResponseMessageDTO {
    @NotEmpty
    String message;

    public ResponseMessageDTO() {
    }

    public ResponseMessageDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
