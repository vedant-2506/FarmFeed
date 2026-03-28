 package com.example.farmFeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FarmFeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmFeedApplication.class, args);
        System.out.println("FarmFeed Started! Visit: http://localhost:8080");
    }
}
