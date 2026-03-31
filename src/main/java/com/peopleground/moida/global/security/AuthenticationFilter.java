package com.peopleground.moida.global.security;

import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.global.exception.ApiErrorCode;
import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.global.exception.ErrorResponse;
import com.peopleground.moida.global.security.jwt.JwtTokenProvider;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.entity.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String LOGIN_PROCESS_URL = "/api/v1/auth/sign-in";

    public AuthenticationFilter(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl(LOGIN_PROCESS_URL);
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws AuthenticationException {

        try {
            FromLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(),
                FromLoginRequest.class);

            UsernamePasswordAuthenticationToken authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                    loginRequest.password());

            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new AppException(UserErrorCode.MEMBER_UNAUTHORIZED);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {

        UUID id = ((CustomUser) authResult.getPrincipal()).getId();
        String username = ((CustomUser) authResult.getPrincipal()).getUsername();
        UserRole role = ((CustomUser) authResult.getPrincipal()).getRole();

        String token = jwtTokenProvider.createToken(id, username, role);
        response.addHeader(AUTHORIZATION_HEADER, token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {

        response.setStatus(UserErrorCode.INVALID_CREDENTIALS.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse errorResponse = ErrorResponse.from(UserErrorCode.INVALID_CREDENTIALS);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
