package com.peopleground.moida.global.security;

public record FromLoginRequest(
    String username,
    String password
) {

}
