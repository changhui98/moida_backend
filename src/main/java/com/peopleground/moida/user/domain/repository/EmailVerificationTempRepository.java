package com.peopleground.moida.user.domain.repository;

import com.peopleground.moida.user.domain.entity.EmailVerificationTemp;
import java.util.Optional;

public interface EmailVerificationTempRepository {

    Optional<EmailVerificationTemp> findByEmail(String email);

    EmailVerificationTemp save(EmailVerificationTemp temp);

    void deleteByEmail(String email);
}
