package com.example.farmFeed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Empty user - not used since all auth is handled via custom endpoints
        return new InMemoryUserDetailsManager();
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