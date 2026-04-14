package com.peopleground.moida.content.presentation.controller;

import com.peopleground.moida.content.application.service.ContentService;
import com.peopleground.moida.content.presentation.dto.request.ContentCreateRequest;
import com.peopleground.moida.content.presentation.dto.response.ContentCreateResponse;
import com.peopleground.moida.global.configure.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contents")
public class ContentController {

    private final ContentService contentService;

    @PostMapping
    public ResponseEntity<ContentCreateResponse> contentCreate(
        @RequestBody ContentCreateRequest req,
        @AuthenticationPrincipal CustomUser user
    ) {
        ContentCreateResponse res = contentService.contentCreate(req, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

}
