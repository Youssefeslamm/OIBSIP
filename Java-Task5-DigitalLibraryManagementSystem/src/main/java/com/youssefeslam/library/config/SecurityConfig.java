package com.youssefeslam.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        return http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
                .authorizeHttpRequests(authorize -> authorize

                        // Public endpoints
                        .requestMatchers(
                                "/api/auth/register",
                                "/error"
                        ).permitAll()

                        // Public catalogue browsing
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/books/**",
                                "/api/categories/**"
                        ).permitAll()

                        // Administrator-only catalogue modifications
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/books/**",
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/books/**",
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/books/**",
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/books/**",
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        // Future administrator endpoints
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Remaining API endpoints require authentication
                        .requestMatchers("/api/**")
                        .authenticated()

                        .anyRequest()
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}