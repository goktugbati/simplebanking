package com.eteration.simplebanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SimplebankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimplebankingApplication.class, args);
    }

}
