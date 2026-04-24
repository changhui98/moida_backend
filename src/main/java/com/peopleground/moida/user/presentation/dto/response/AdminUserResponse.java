package com.peopleground.moida.user.presentation.dto.response;

import com.peopleground.moida.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminUserResponse(
    UUID id,
    String username,
    String nickname,
    String userEmail,
    String address,
    boolean isDeleted,
    LocalDateTime createdDate,
    LocalDateTime modifiedDate
) implements UserResponseMarker{

    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getUserEmail(),
            user.getAddress(),
            user.isDeleted(),
            user.getCreatedDate(),
            user.getLastModifiedDate()
        );
    }

}
