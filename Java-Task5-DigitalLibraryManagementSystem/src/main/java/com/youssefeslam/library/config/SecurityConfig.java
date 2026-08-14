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
    SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )

                .authorizeHttpRequests(authorize -> authorize

                        // Public API endpoints
                        .requestMatchers(
                                "/api/auth/register",
                                "/error"
                        ).permitAll()

                        // Public catalogue browsing API
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/books/**",
                                "/api/categories/**"
                        ).permitAll()

                        // Administrator-only catalogue changes
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

                        // Administrator API
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Remaining API endpoints require authentication
                        .requestMatchers("/api/**")
                        .authenticated()

                        // Public web pages and static assets
                        .requestMatchers(
                                "/",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/error"
                        ).permitAll()

                        // Every other web page requires authentication
                        .anyRequest()
                        .authenticated()
                )

                // Custom Thymeleaf login page
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl(
                                "/dashboard",
                                true
                        )
                        .permitAll()
                )

                // Thymeleaf logout form
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                // Retain Basic Auth for Postman
                .httpBasic(Customizer.withDefaults())

                .build();
    }
}