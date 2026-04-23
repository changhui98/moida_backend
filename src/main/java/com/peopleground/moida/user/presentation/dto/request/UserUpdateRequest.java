package com.peopleground.moida.user.presentation.dto.request;

public record UserUpdateRequest(
    String nickname,
    String userEmail,
    String address,
    String currentPassword,
    String newPassword,
    String profileImageUrl
) {

}
