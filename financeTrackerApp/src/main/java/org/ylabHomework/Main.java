package org.ylabHomework;

import org.logging.EnableLogging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableLogging
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}