package com.peopleground.moida.user.domain;

import com.peopleground.moida.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "조회하려는 사용자가 존재하지 않습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "U002", "이미 존재하는 아이디입니다."),
    MEMBER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "U003", "인증되지 않은 사용자입니다."),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "U004", "아이디 또는 비밀번호가 올바르지 않습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
