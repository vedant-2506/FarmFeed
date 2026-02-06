// package com.example.farmFeed;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class FarmFeedApplication {

// 	public static void main(String[] args) {
// 		SpringApplication.run(FarmFeedApplication.class, args);
// 	}

// }
package com.example.farmFeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FarmFeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmFeedApplication.class, args);
        System.out.println("farmFeed Application Started Successfully!");
        System.out.println("Server running at: http://localhost:9090");
        System.out.println("Frontend: http://localhost:9090/Home.html");
        System.out.println("API Base: http://localhost:9090/api/fertilizers");
        System.out.println("Farmer SignUp: http://localhost:9090/api/Farmer/SignUp");
        System.out.println("DB Test: http://localhost:9090/db-test");
    }
}
