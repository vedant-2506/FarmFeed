// package com.example.farmFeed;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class FarmFeedApplication {

// 	public static void main(String[] args) {
// 		SpringApplication.run(FarmFeedApplication.class, args);
// 	}

// }



// package com.example.farmFeed;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class FarmFeedApplication {

//     public static void main(String[] args) {
//         SpringApplication.run(FarmFeedApplication.class, args);
//         System.out.println("farmFeed Application Started Successfully!");
//         System.out.println("Server running at: http://localhost:9090");
//         System.out.println("Frontend: http://localhost:9090/Home.html");
//         System.out.println("API Base: http://localhost:9090/api/fertilizers");
//         System.out.println("Farmer SignUp: http://localhost:9090/api/Farmer/SignUp");
//         System.out.println("DB Test: http://localhost:9090/db-test");
//     }
// }


server.port=${PORT:8080}

spring.datasource.url=jdbc:mysql://HOST:3306/farmfeed
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect