package com.peopleground.sagwim.global.security;

import com.peopleground.sagwim.global.configure.CorsConfig;
import com.peopleground.sagwim.global.exception.JsonAccessDeniedHandler;
import com.peopleground.sagwim.global.exception.JsonAuthenticationEntryPoint;
import com.peopleground.sagwim.global.redis.TokenBlacklistService;
import com.peopleground.sagwim.global.security.jwt.JwtAuthenticationFilter;
import com.peopleground.sagwim.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final TokenBlacklistService tokenBlacklistService;

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
                auth
                    // Docker 헬스체크 및 모니터링 (인증 불필요)
                    .requestMatchers("/actuator/health").permitAll()
                    // Swagger UI (개발/운영 모두 접근 허용 — 필요 시 prod 프로파일에서 비활성화)
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    // 로그아웃은 인증 필요 (토큰을 블랙리스트에 등록해야 하므로)
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/sign-out").authenticated()
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    // 태그 관련 공개 API (비로그인 사용자도 태그 검색 가능)
                    .requestMatchers("/api/v1/tags/**").permitAll()
                    // 댓글 목록 조회 공개 API (비로그인 사용자도 댓글 조회 가능)
                    .requestMatchers(HttpMethod.GET, "/api/v1/contents/*/comments").permitAll()
                    // 이미지 조회 공개 API (비로그인 사용자도 이미지 조회 가능)
                    .requestMatchers(HttpMethod.GET, "/api/v1/images").permitAll()
                    .requestMatchers("/images/**").permitAll()
                    .anyRequest().authenticated())

            .exceptionHandling(e ->
                e.accessDeniedHandler(new JsonAccessDeniedHandler())
                    .authenticationEntryPoint(new JsonAuthenticationEntryPoint()))

            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, tokenBlacklistService), AuthenticationFilter.class)

            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
    }
}
