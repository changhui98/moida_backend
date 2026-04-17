package com.peopleground.moida.content.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminContentUpdateRequest(
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 20, message = "제목은 20자 이하로 입력해주세요.")
    String title,

    @NotBlank(message = "내용을 입력해주세요.")
    String body
) {

}
