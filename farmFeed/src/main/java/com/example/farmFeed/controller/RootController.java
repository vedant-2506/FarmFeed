package com.example.farmFeed.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class RootController {

    /**
     * Handle root path "/" and serve static content info
     */
    @GetMapping("/")
    public String root() {
        return "Welcome to FarmFeed! Access the application at: http://localhost:9090/Home.html";
    }

    /**
     * Redirect /index to Home.html
     */
    @GetMapping("/index")
    public String index() {
        return "FarmFeed Application - Please navigate to /Home.html";
    }

    /**
     * Serve a favicon to avoid browser 404 requests for /favicon.ico.
     */
    @GetMapping("/favicon.ico")
    public ResponseEntity<Resource> favicon() {
        Resource favicon = new ClassPathResource("static/logo.png");
        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(favicon);
    }
}
