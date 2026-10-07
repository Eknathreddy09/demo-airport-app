package com.airport.grms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GrmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GrmsApplication.class, args);
    }
}
