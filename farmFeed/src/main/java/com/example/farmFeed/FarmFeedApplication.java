package com.example.farmFeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FarmFeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmFeedApplication.class, args);
       String line = "=".repeat(60);

        System.out.println("\n" + line);
        System.out.println("   farmFeed Application Started Successfully!");
        System.out.println("   Server running at: http://localhost:9090");
        System.out.println(line);

        System.out.println("\n FRONTEND PAGES:");
        System.out.println("   Home         : http://localhost:9090/Home.html");
        System.out.println("   Farmer Login : http://localhost:9090/Login.html");
        System.out.println("   Farmer SignUp: http://localhost:9090/SignUp_Farmer.html");
        System.out.println("   Shop SignUp  : http://localhost:9090/SignUp_Shopkeeper.html");
        System.out.println("   Cart         : http://localhost:9090/Cart.html");

        System.out.println("\n FARMER APIs  (/api/auth):");
        System.out.println("   POST   /api/auth/register                  → Register farmer");
        System.out.println("   POST   /api/auth/login                     → Farmer login");
        System.out.println("   GET    /api/auth/profile/{farmerId}        → Get farmer profile");
        System.out.println("   PUT    /api/auth/change-password/{farmerId}→ Change password");

        System.out.println("\n SHOPKEEPER APIs  (/api/shopkeeper):");
        System.out.println("   POST   /api/shopkeeper/register                     → Register shopkeeper");
        System.out.println("   POST   /api/shopkeeper/login                        → Shopkeeper login");
        System.out.println("   GET    /api/shopkeeper/profile/{shopkeeperId}       → Get profile");
        System.out.println("   PUT    /api/shopkeeper/change-password/{id}         → Change password");

        System.out.println("\n FERTILIZER APIs  (/api/fertilizers):");
        System.out.println("   GET    /api/fertilizers                    → Get all fertilizers");
        System.out.println("   GET    /api/fertilizers/{id}               → Get fertilizer by ID");
        System.out.println("   GET    /api/fertilizers/search?name=urea   → Search by name");

        System.out.println("\n UTILITY:");
        System.out.println("   GET    /db-test                            → Test DB connection");

        System.out.println("\n" + line + "\n");
    }
}
