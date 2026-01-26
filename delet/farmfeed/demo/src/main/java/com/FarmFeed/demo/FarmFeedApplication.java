package com.FarmFeed.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class FarmFeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmFeedApplication.class, args);
    }

    @Configuration
    public static class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // Serve static files from MY folder
            registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/MY/")
                    .setCachePeriod(3600);
        }
    }
}
