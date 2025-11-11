package org.anyatasks.notifier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender(SMTPProps smtpProps) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(smtpProps.getHost());
        mailSender.setPort(smtpProps.getPort());
        mailSender.setUsername(smtpProps.getUsername());
        mailSender.setPassword(smtpProps.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", smtpProps.getProtocol());
        props.put("mail.smtp.auth", true);

        return mailSender;
    }
}