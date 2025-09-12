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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
            .cors(cors -> {}) // enable CORS (configured separately in CorsConfig)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Public endpoints
                .requestMatchers("/api/login").permitAll()
                .requestMatchers("/api/signup").permitAll()
    
                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/webjars/**"
                ).permitAll()

                // User endpoints
                .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/{id}")
                    .hasAnyAuthority("Manager", "Admin", "Developer", "Tester")
                .requestMatchers("/api/users/**")
                    .hasAnyAuthority("Manager", "Admin", "Developer", "Tester")
                .requestMatchers("api/users/{id}")
                    .hasAuthority("Admin")

                // Bug endpoints
                .requestMatchers(HttpMethod.PUT, "/api/bugs")
                    .hasAnyAuthority("Manager", "Admin", "Developer", "Tester")
                .requestMatchers(HttpMethod.POST, "/api/bugs")
                    .hasAnyAuthority("Manager", "Admin")
                .requestMatchers(HttpMethod.DELETE, "/api/bugs/**")
                    .hasAnyAuthority("Manager", "Admin")
                .requestMatchers(HttpMethod.GET, "/api/bugs/**")
                    .hasAnyAuthority("Manager", "Admin", "Developer", "Tester")

                // Project endpoints
                .requestMatchers(HttpMethod.GET, "/api/projects", "/api/projects/**")
                    .hasAnyAuthority("Manager", "Admin", "Developer", "Tester")
                .requestMatchers(HttpMethod.PUT, "/api/projects")
                    .hasAnyAuthority("Manager", "Admin", "Developer", "Tester")
                .requestMatchers(HttpMethod.POST, "/api/projects")
                    .hasAuthority("Admin")
                .requestMatchers(HttpMethod.DELETE, "/api/projects/**")
                    .hasAuthority("Admin")

                // Fallback
                .anyRequest().hasAuthority("Admin")
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
       @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization") // <--- important
                        .allowCredentials(true);
            }
        };
}
}
