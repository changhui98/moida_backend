package com.peopleground.moida.content.presentation.dto.response;

import com.peopleground.moida.content.domain.entity.Content;
import java.time.LocalDateTime;

public record ContentResponse(
    Long id,
    String title,
    String body,
    String createdBy,
    LocalDateTime createdAt
) {
    public static ContentResponse from(Content content) {
        return new ContentResponse(
            content.getId(),
            content.getTitle(),
            content.getBody(),
            content.getCreatedBy(),
            content.getCreatedDate()
        );
    }
}
