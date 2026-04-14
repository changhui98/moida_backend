package com.peopleground.moida.content.presentation.dto.response;

import com.peopleground.moida.content.domain.entity.Content;
import java.time.LocalDateTime;

public record ContentCreateResponse(
    Long id,
    String title,
    String body,
    String createdBy,
    LocalDateTime createdAt,
    String updatedBy,
    LocalDateTime updatedAt
) {
    public static ContentCreateResponse from(Content content) {

        return new ContentCreateResponse(
            content.getId(),
            content.getTitle(),
            content.getBody(),
            content.getUser().getUsername(),
            content.getCreatedDate(),
            content.getUser().getUsername(),
            content.getLastModifiedDate()
        );
    }

}
