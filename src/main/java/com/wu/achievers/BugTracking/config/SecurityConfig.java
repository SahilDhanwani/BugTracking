package com.wu.achievers.BugTracking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wu.achievers.BugTracking.filter.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/{id}").hasAnyRole("MANAGER", "ADMIN", "DEVELOPER", "TESTER")
                .requestMatchers("/api/signup", "api/users/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,"/api/bugs").hasAnyRole("MANAGER", "ADMIN", "DEVELOPER", "TESTER")
                .requestMatchers(HttpMethod.POST, "/api/bugs").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/bugs/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET,  "/api/bugs/**").hasAnyRole("MANAGER", "ADMIN", "DEVELOPER", "TESTER")
                .anyRequest().hasRole("ADMIN")
        )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
