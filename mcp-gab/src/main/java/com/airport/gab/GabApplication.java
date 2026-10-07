package com.airport.gab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GabApplication {
    public static void main(String[] args) {
        SpringApplication.run(GabApplication.class, args);
    }
}
