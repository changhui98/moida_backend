package com.peopleground.moida.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank(message = "username은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "username은 영어만 입력 가능합니다.")
    String username,

    @NotBlank(message = "password는 필수입니다.")
    @Size(min = 8, message = "password는 최소 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]).+$", message = "password는 대문자, 소문자, 특수문자를 각각 하나 이상 포함해야 합니다.")
    String password,

    @NotBlank
    String address

) {
}
