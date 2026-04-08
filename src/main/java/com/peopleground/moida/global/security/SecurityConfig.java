package com.peopleground.moida.global.security;

import com.peopleground.moida.global.configure.CorsConfig;
import com.peopleground.moida.global.exception.JsonAccessDeniedHandler;
import com.peopleground.moida.global.exception.JsonAuthenticationEntryPoint;
import com.peopleground.moida.global.security.jwt.JwtAuthenticationFilter;
import com.peopleground.moida.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg)
        throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public AuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter(jwtTokenProvider, objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, AuthenticationFilter authenticationFilter) throws Exception {

        http
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeHttpRequests(auth ->
                auth.requestMatchers("/api/v1/auth/**").permitAll()
                    .anyRequest().authenticated())

            .exceptionHandling(e ->
                e.accessDeniedHandler(new JsonAccessDeniedHandler())
                    .authenticationEntryPoint(new JsonAuthenticationEntryPoint()))

            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), AuthenticationFilter.class)

            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
    }
}
