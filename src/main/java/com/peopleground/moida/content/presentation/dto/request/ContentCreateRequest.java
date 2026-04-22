package com.peopleground.moida.content.presentation.dto.request;

import java.util.List;

public record ContentCreateRequest(
    String title,
    String body,
    List<String> tags   // 선택 필드: null 또는 빈 리스트이면 태그 없는 게시글로 처리
) {

}
