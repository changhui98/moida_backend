package com.peopleground.moida.global.exception;

public record ApiResponse(
    String errorCode,
    String message
) {

}
