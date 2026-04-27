package com.peopleground.moida.user.presentation.dto.response;

import com.peopleground.moida.user.domain.entity.OAuthProvider;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.entity.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDetailResponse(
    UUID id,
    String username,
    String nickname,
    String userEmail,
    String address,
    UserRole role,
    String profileImageUrl,
    OAuthProvider provider,
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
            user.getRole(),
            user.getProfileImageUrl(),
            user.getProvider(),
            user.getCreatedDate(),
            user.getLastModifiedDate()
        );
    }

}
