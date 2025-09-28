package org.anyatasks.notifier.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class SMTPProps {
    String protocol;
    String host;
    int port;
    String username;
    String password;
}
