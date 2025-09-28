package org.anyatasks.notifier.dtos;

import lombok.Data;

@Data
public class UserRegisteredEvent {
    private Long userId;
    private String username;
    private String email;
}
