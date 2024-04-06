package io.summarizeit.backend.config;

import io.summarizeit.backend.security.JwtAuthenticationEntryPoint;
import io.summarizeit.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        /**
         * Configure Spring Security.
         *
         * @param http HttpSecurity
         * @return SecurityFilterChain
         */
        @Bean
        public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .exceptionHandling(configurer -> configurer
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                                .sessionManagement(configurer -> configurer
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .headers(configurer -> configurer
                                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .authorizeHttpRequests(requests -> requests
                                                .requestMatchers(
                                                                "/",
                                                                "/auth/**",
                                                                "/public/**",
                                                                "/assets/**",
                                                                "/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/webjars/**",
                                                                "/actuator/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .build();
        }
}
