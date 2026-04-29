package com.peopleground.moida.like.presentation.dto.response;

import com.peopleground.moida.user.domain.entity.User;

public record GroupLikerResponse(
    String username,
    String nickname,
    String profileImageUrl
) {

    public static GroupLikerResponse from(User user) {
        return new GroupLikerResponse(
            user.getUsername(),
            user.getNickname(),
            user.getProfileImageUrl()
        );
    }
}
