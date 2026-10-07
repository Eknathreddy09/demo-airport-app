package com.airport.digifly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DigiflyApplication {
    public static void main(String[] args) {
        SpringApplication.run(DigiflyApplication.class, args);
    }
}
