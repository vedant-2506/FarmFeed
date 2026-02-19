package com.example.farmFeed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ============================================================
 * FarmFeed Security Configuration
 * ============================================================
 * 
 * CRITICAL FILE - Application won't start without this!
 * 
 * Purpose:
 * 1. Provides BCrypt password encoder bean (used by AuthService & ShopkeeperService)
 * 2. Configures HTTP security to allow public access to:
 *    - All static resources (HTML, CSS, JS)
 *    - Authentication endpoints (login, register)
 *    - Fertilizer API endpoints
 * 3. Disables CSRF for REST API
 * 
 * Location: src/main/java/com/example/farmFeed/config/SecurityConfig.java
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * BCrypt Password Encoder Bean
     * 
     * Strength: 12 (2^12 = 4096 rounds of hashing)
     * This is injected into:
     * - AuthService (farmer authentication)
     * - ShopkeeperService (shopkeeper authentication)
     * 
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * HTTP Security Filter Chain
     * 
     * Configures which URLs are public vs protected
     * For your FarmFeed app, most endpoints are public
     * 
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST API (required for POST requests to work)
            .csrf(csrf -> csrf.disable())
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow ALL static resources (HTML, CSS, JS, images)
                .requestMatchers(
                    "/",
                    "/Home.html",
                    "/Login.html",
                    "/SignUp_Farmer.html",
                    "/SignUp_Shopkeeper.html",
                    "/Cart.html",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                
                // Allow farmer authentication endpoints
                .requestMatchers(
                    "/api/auth/**",           // Farmer registration & login
                    "/api/Farmer/**"          // Old farmer endpoints (if used)
                ).permitAll()
                
                // Allow shopkeeper authentication endpoints
                .requestMatchers("/api/shopkeeper/**").permitAll()
                
                // Allow fertilizer API (product browsing)
                .requestMatchers("/api/fertilizers/**").permitAll()
                
                // Allow DB test endpoint (for debugging)
                .requestMatchers("/db-test").permitAll()
                
                // Require authentication for everything else
                .anyRequest().authenticated()
            )
            
            // Stateless session (no cookies, good for REST API)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Allow same-origin framing (if needed for iframes)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );
        return http.build();
    }
}