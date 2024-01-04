package com.example.easytourneybe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EasyTourneyBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyTourneyBeApplication.class, args);
    }
}
