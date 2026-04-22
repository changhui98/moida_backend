package com.peopleground.moida.content.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ContentUpdateRequest(
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 20, message = "제목은 20자 이하로 입력해주세요.")
    String title,

    @NotBlank(message = "내용을 입력해주세요.")
    String body,

    List<String> tags   // 선택 필드: null 이면 태그 변경 없음, 빈 리스트이면 태그 전체 제거
) {

}
