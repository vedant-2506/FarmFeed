package com.example.farmFeed.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RootController {

    /**
     * Handle root path "/" and serve static content info
     */
    @GetMapping("/")
    @ResponseBody
    public String root() {
        return "Welcome to FarmFeed! Access the application at: http://localhost:9090/Home.html";
    }

    /**
     * Redirect /index to Home.html
     */
    @GetMapping("/index")
    @ResponseBody
    public String index() {
        return "FarmFeed Application - Please navigate to /Home.html";
    }
}
