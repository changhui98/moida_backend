package com.peopleground.moida.user.presentation.dto.request;

public record SocialSignInRequest(
    String provider,
    String code,
    String redirectUri
) {}
