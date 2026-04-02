package com.peopleground.moida.user.presentation.dto.response;

import com.peopleground.moida.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDetailResponse(
    UUID id,
    String username,
    String nickname,
    String userEmail,
    String address,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {

    public static UserDetailResponse from(User user) {
        return new UserDetailResponse(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getUserEmail(),
            user.getAddress(),
            user.getCreatedDate(),
            user.getLastModifiedDate()
        );
    }

}
