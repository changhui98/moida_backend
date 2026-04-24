package com.peopleground.moida.content.presentation.dto.response;

import com.peopleground.moida.content.domain.entity.Content;
import java.time.LocalDateTime;
import java.util.List;

public record ContentResponse(
    Long id,
    String title,
    String body,
    String createdBy,
    String nickname,
    LocalDateTime createdAt,
    int likeCount,
    int commentCount,
    boolean likedByMe,
    List<String> tags,
    List<String> imageUrls
) {
    public static ContentResponse from(Content content) {
        return from(content, null, false, List.of(), List.of());
    }

    public static ContentResponse from(Content content, boolean likedByMe) {
        return from(content, null, likedByMe, List.of(), List.of());
    }

    public static ContentResponse from(Content content, String nickname, boolean likedByMe) {
        return from(content, nickname, likedByMe, List.of(), List.of());
    }

    // 기존 4-파라미터 오버로드 유지 (ContentResponseAssembler가 이 시그니처 호출 중)
    public static ContentResponse from(Content content, String nickname, boolean likedByMe, List<String> tags) {
        return from(content, nickname, likedByMe, tags, List.of());
    }

    // 새 5-파라미터 오버로드 추가
    public static ContentResponse from(
        Content content,
        String nickname,
        boolean likedByMe,
        List<String> tags,
        List<String> imageUrls
    ) {
        return new ContentResponse(
            content.getId(),
            content.getTitle(),
            content.getBody(),
            content.getCreatedBy(),
            nickname,
            content.getCreatedDate(),
            content.getLikeCount(),
            content.getCommentCount(),
            likedByMe,
            tags == null ? List.of() : List.copyOf(tags),
            imageUrls == null ? List.of() : List.copyOf(imageUrls)
        );
    }
}
