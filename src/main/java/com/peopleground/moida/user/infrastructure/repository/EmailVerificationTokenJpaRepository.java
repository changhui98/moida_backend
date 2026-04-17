package com.peopleground.moida.user.infrastructure.repository;

import com.peopleground.moida.user.domain.entity.EmailVerificationToken;
import com.peopleground.moida.user.domain.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenJpaRepository
    extends JpaRepository<EmailVerificationToken, UUID> {

    Optional<EmailVerificationToken> findByUserUserEmail(String email);

    void deleteByUser(User user);
}
