package com.peopleground.moida.content.presentation.dto.response;

import com.peopleground.moida.content.domain.entity.Content;
import java.time.LocalDateTime;

public record ContentUpdateResponse(
    Long id,
    String title,
    String body,
    String author,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ContentUpdateResponse from(Content content) {

        return new ContentUpdateResponse(
            content.getId(),
            content.getTitle(),
            content.getBody(),
            content.getUser().getUsername(),
            content.getCreatedDate(),
            content.getLastModifiedDate()
        );
    }
}
