package com.peopleground.moida.content.presentation.controller;

import com.peopleground.moida.content.application.service.AdminService;
import com.peopleground.moida.content.presentation.dto.response.AdminContentResponse;
import com.peopleground.moida.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/contents")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AdminContentResponse>> getAllContents(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<AdminContentResponse> res = adminService.getAllContents(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
