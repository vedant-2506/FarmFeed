package com.example.farmFeed;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                            "https://farmfeed-sigma.vercel.app",
                            "http://localhost:3000",
                            "http://localhost:9090",
                            "http://localhost:8080"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

---

## What Happens Next

1. Commit this file to GitHub
2. Render **auto-detects** the change and rebuilds (~3-5 mins)
3. Once Render finishes → Vercel frontend can talk to backend ✅

---

## 🧪 How to Confirm It's Working

Open browser → go to your Vercel site → press **F12** → Console tab.

**Before fix you see:**
```
Access to fetch blocked by CORS policy ❌
```

**After fix you see:**
```
200 OK ✅
