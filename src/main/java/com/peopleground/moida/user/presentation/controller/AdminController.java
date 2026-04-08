package com.peopleground.moida.user.presentation.controller;

import com.peopleground.moida.global.dto.PageResponse;
import com.peopleground.moida.user.application.AdminService;
import com.peopleground.moida.user.presentation.dto.response.AdminUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AdminUserResponse>> getUsersForAdmin(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<AdminUserResponse> res = adminService.getUsersForAdmin(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }



}
