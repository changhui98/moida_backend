package com.peopleground.moida.image.presentation.dto.response;

import com.peopleground.moida.image.domain.entity.Image;
import com.peopleground.moida.image.domain.entity.ImageTargetType;
import java.time.LocalDateTime;

public record ImageResponse(
    Long id,
    ImageTargetType targetType,
    String targetId,
    String originalFilename,
    String fileUrl,
    long fileSize,
    String contentType,
    int sortOrder,
    LocalDateTime createdDate
) {

    public static ImageResponse from(Image image) {
        return new ImageResponse(
            image.getId(),
            image.getTargetType(),
            image.getTargetId(),
            image.getOriginalFilename(),
            image.getFileUrl(),
            image.getFileSize(),
            image.getContentType(),
            image.getSortOrder(),
            image.getCreatedDate()
        );
    }
}
