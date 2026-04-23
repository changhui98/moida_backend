package com.peopleground.moida.content.presentation.dto.response;

import com.peopleground.moida.content.domain.entity.Content;
import java.time.LocalDateTime;

public record ContentResponse(
    Long id,
    String title,
    String body,
    String createdBy,
    String nickname,
    LocalDateTime createdAt,
    int likeCount,
    int commentCount,
    boolean likedByMe
) {
    public static ContentResponse from(Content content) {
        return from(content, null, false);
    }

    public static ContentResponse from(Content content, boolean likedByMe) {
        return from(content, null, likedByMe);
    }

    public static ContentResponse from(Content content, String nickname, boolean likedByMe) {
        return new ContentResponse(
            content.getId(),
            content.getTitle(),
            content.getBody(),
            content.getCreatedBy(),
            nickname,
            content.getCreatedDate(),
            content.getLikeCount(),
            content.getCommentCount(),
            likedByMe
        );
    }
}
