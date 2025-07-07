package org.example.backend;

import org.example.backend.utils.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        EnvLoader.load();
        SpringApplication.run(BackendApplication.class, args);
    }

}
