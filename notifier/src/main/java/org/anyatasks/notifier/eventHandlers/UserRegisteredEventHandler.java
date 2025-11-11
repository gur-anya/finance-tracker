package org.anyatasks.notifier.eventHandlers;

import lombok.RequiredArgsConstructor;
import org.anyaTasks.DTOs.Event;
import org.anyaTasks.DTOs.UserRegisteredEvent;
import org.anyatasks.notifier.services.EmailSendingService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegisteredEventHandler implements EventHandler<UserRegisteredEvent> {

    private final EmailSendingService emailSendingService;
    private static final String WELCOME_EMAIL_SUBJECT = "Добро пожаловать!";
    private static final String WELCOME_EMAIL_TEMPLATE = """
        Здравствуйте, %s!
        Добро пожаловать в трекер финансов! Теперь вы сможете отслеживать финансовые транзакции,
        просматривать финансовую аналитику и статистику, а также устанавливать бюджет и цель для накопления средств.
        """;
    @Override
    public void handle(Event<UserRegisteredEvent> event) {
        UserRegisteredEvent userEventData = event.getEventData();
        String greetingMessage = String.format(WELCOME_EMAIL_TEMPLATE, userEventData.getUsername());
        emailSendingService.send(userEventData.getEmail(), WELCOME_EMAIL_SUBJECT, greetingMessage);
    }

    @Override
    public String getEventType() {
        return "user.successful-registration";
    }
}
