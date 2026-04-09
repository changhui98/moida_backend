package com.peopleground.moida.user.presentation.dto.response;

import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.entity.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminUserDetailResponse(
    UUID id,
    String username,
    String nickname,
    String userEmail,
    UserRole role,
    String address,
    LocalDateTime createAt,
    LocalDateTime modifiedAt,
    boolean isDeleted,
    LocalDateTime deletedAt
) {
    public static AdminUserDetailResponse from(User user) {
        return new AdminUserDetailResponse(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getUserEmail(),
            user.getRole(),
            user.getAddress(),
            user.getCreatedDate(),
            user.getLastModifiedDate(),
            user.isDeleted(),
            user.getDeletedDate()
        );
    }
}
