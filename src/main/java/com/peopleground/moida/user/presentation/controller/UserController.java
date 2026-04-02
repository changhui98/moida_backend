package com.peopleground.moida.user.presentation.controller;

import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.global.dto.PageResponse;
import com.peopleground.moida.user.application.UserService;
import com.peopleground.moida.user.presentation.dto.response.UserDetailResponse;
import com.peopleground.moida.user.presentation.dto.response.UserResponseMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponse<UserResponseMarker>> getUsers(
        @AuthenticationPrincipal CustomUser customUser,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<UserResponseMarker> res = userService.getUsers(customUser, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailResponse> getMyProfile(
        @AuthenticationPrincipal CustomUser customUser
    ) {
        UserDetailResponse res = userService.getMyProfile(customUser);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(
        @AuthenticationPrincipal CustomUser customUser
    ) {
        userService.deleteUser(customUser);

        return ResponseEntity.noContent().build();
    }

}
