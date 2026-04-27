package com.peopleground.moida.user.presentation.dto.response;

public record SocialSignInResponse(
    String jwtToken,
    boolean isNewUser,
    String nickname
) {}
