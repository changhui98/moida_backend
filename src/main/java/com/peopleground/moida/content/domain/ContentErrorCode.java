package com.peopleground.moida.content.domain;

import com.peopleground.moida.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ContentErrorCode implements ErrorCode {

    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "존재하지 않는 게시글입니다."),
    CONTENT_FORBIDDEN(HttpStatus.FORBIDDEN, "C002", "게시글을 수정할 권한이 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
