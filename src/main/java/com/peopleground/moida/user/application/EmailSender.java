package com.peopleground.moida.user.application;

public interface EmailSender {

    void sendVerificationEmail(String toEmail, String code);
}
