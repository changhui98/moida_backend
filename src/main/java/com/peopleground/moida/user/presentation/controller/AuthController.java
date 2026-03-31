package com.peopleground.moida.user.presentation.controller;

import com.peopleground.moida.user.application.AuthService;
import com.peopleground.moida.user.presentation.dto.request.UserCreateRequest;
import com.peopleground.moida.user.presentation.dto.response.UserCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserCreateResponse> signUp(
        @RequestBody @Valid UserCreateRequest request
    ) {
        UserCreateResponse res = authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

}
