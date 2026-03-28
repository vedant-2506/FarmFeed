package com.example.farmFeed.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // CSS resources
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCachePeriod(3600);
        
        // JS resources
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(3600);
        
        // HTML and other static files (but NOT /api/*)
        registry.addResourceHandler("/*.html", "/*.png", "/*.jpg", "/*.jpeg", "/*.gif", "/*.css", "/*.js", "/favicon.ico")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}
