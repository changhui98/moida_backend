package com.peopleground.moida.like.presentation.controller;

import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.like.application.service.LikeService;
import com.peopleground.moida.like.presentation.dto.response.LikeStatusResponse;
import com.peopleground.moida.like.presentation.dto.response.LikeToggleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups/{groupId}/likes")
public class GroupLikeController {

    private final LikeService likeService;

    /**
     * 모임 좋아요 토글 (로그인 필요)
     */
    @PostMapping
    public ResponseEntity<LikeToggleResponse> toggleGroupLike(
        @PathVariable Long groupId,
        @AuthenticationPrincipal CustomUser user
    ) {
        LikeToggleResponse res = likeService.toggleGroupLike(groupId, user);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    /**
     * 내 모임 좋아요 여부 확인 (로그인 필요)
     */
    @GetMapping("/me")
    public ResponseEntity<LikeStatusResponse> getGroupLikeStatus(
        @PathVariable Long groupId,
        @AuthenticationPrincipal CustomUser user
    ) {
        LikeStatusResponse res = likeService.getGroupLikeStatus(groupId, user);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
