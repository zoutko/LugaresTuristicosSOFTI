package com.proyecto.app.security.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
// @EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
            AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/recover-password",
                                "/api/auth/change-password",
                                "/api/users/register",
                                "/api/users/{userId}",
                                "/api/users/{userId}/saved-tours",
                                "/api/tours",
                                "/places/**",
                                "/h2-console/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tours/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/tours/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/tours/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/tours/**").hasRole("ADMINISTRATOR")
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

        @Bean
        public CorsConfigurationSource corsConfigurationSource(
                        @Value("${app.cors.allowed-origins:http://localhost:4200}") String allowedOriginsCsv) {

                List<String> allowedOrigins = Arrays.stream(allowedOriginsCsv.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isBlank())
                                .toList();

                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(allowedOrigins);
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
                config.setExposedHeaders(List.of("Authorization"));
                config.setAllowCredentials(false);
                config.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }
}