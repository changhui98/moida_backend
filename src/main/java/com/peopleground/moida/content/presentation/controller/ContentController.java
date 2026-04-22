package com.peopleground.moida.content.presentation.controller;

import com.peopleground.moida.content.application.service.ContentService;
import com.peopleground.moida.content.presentation.dto.request.ContentCreateRequest;
import com.peopleground.moida.content.presentation.dto.request.ContentUpdateRequest;
import com.peopleground.moida.content.presentation.dto.request.SearchType;
import com.peopleground.moida.content.presentation.dto.response.ContentCreateResponse;
import com.peopleground.moida.content.presentation.dto.response.ContentResponse;
import com.peopleground.moida.content.presentation.dto.response.ContentUpdateResponse;
import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.global.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contents")
public class ContentController {

    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<PageResponse<ContentResponse>> getContents(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "TITLE") SearchType searchType,
        @AuthenticationPrincipal CustomUser user
    ) {
        PageResponse<ContentResponse> res = contentService.getContents(page, size, keyword, searchType, user);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    /**
     * 현재 로그인한 사용자가 작성한 글 목록(최신순, 삭제글 제외) 을 반환한다.
     */
    @GetMapping("/me")
    public ResponseEntity<PageResponse<ContentResponse>> getMyContents(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @AuthenticationPrincipal CustomUser user
    ) {
        PageResponse<ContentResponse> res = contentService.getMyContents(user, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping
    public ResponseEntity<ContentCreateResponse> contentCreate(
        @RequestBody ContentCreateRequest req,
        @AuthenticationPrincipal CustomUser user
    ) {
        ContentCreateResponse res = contentService.contentCreate(req, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/{contentId}")
    public ResponseEntity<ContentUpdateResponse> updateContent(
        @PathVariable Long contentId,
        @Valid @RequestBody ContentUpdateRequest req,
        @AuthenticationPrincipal CustomUser user
    ) {
        ContentUpdateResponse res = contentService.updateContent(contentId, req, user);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteContent(
        @PathVariable Long contentId,
        @AuthenticationPrincipal CustomUser user
    ) {
        contentService.deleteContent(contentId, user);
        return ResponseEntity.noContent().build();
    }

}
