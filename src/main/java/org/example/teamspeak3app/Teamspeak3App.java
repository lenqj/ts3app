package org.example.teamspeak3app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Teamspeak3App {
    public static void main(String[] args) {
        SpringApplication.run(Teamspeak3App.class, args);
    }
}
