package com.peopleground.moida.user.domain.repository;

import com.peopleground.moida.user.domain.entity.EmailVerificationToken;
import com.peopleground.moida.user.domain.entity.User;
import java.util.Optional;

public interface EmailVerificationTokenRepository {

    Optional<EmailVerificationToken> findByUserEmail(String email);

    void deleteByUser(User user);

    EmailVerificationToken save(EmailVerificationToken token);
}
