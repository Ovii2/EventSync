package org.example.backend;

import org.example.backend.utils.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@EnableAsync
@EnableWebSecurity
@EnableMethodSecurity
@EnableWebSocketMessageBroker
@EnableScheduling
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        EnvLoader.load();
        SpringApplication.run(BackendApplication.class, args);
    }
}
