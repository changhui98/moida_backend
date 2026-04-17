package com.peopleground.moida.user.application;

import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.entity.EmailVerificationToken;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.repository.EmailVerificationTokenRepository;
import com.peopleground.moida.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailSender emailSender;

    @Transactional
    public void sendVerificationEmail(String userEmail) {

        User user = userRepository.findByUserEmail(userEmail).orElseThrow(
            () -> new AppException(UserErrorCode.USER_NOT_FOUND)
        );

        if (user.isEmailVerified()) {
            throw new AppException(UserErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        String code = String.format("%06d", new Random().nextInt(1000000));

        emailVerificationTokenRepository.findByUserEmail(userEmail)
            .ifPresent(token -> emailVerificationTokenRepository.deleteByUser(user));

        EmailVerificationToken token = EmailVerificationToken.of(user, code);
        emailVerificationTokenRepository.save(token);

        emailSender.sendVerificationEmail(userEmail, code);
    }

    @Transactional
    public void verifyCode(String userEmail, String code) {

        EmailVerificationToken token = emailVerificationTokenRepository.findByUserEmail(userEmail)
            .orElseThrow(() -> new AppException(UserErrorCode.INVALID_VERIFICATION_CODE));

        if (token.isExpired()) {
            User user = userRepository.findByUserEmail(userEmail).orElseThrow(
                () -> new AppException(UserErrorCode.USER_NOT_FOUND)
            );
            emailVerificationTokenRepository.deleteByUser(user);
            throw new AppException(UserErrorCode.INVALID_VERIFICATION_CODE);
        }

        if (!token.getCode().equals(code)) {
            throw new AppException(UserErrorCode.INVALID_VERIFICATION_CODE);
        }

        User user = userRepository.findByUserEmail(userEmail).orElseThrow(
            () -> new AppException(UserErrorCode.USER_NOT_FOUND)
        );
        user.verifyEmail();

        emailVerificationTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void resendCode(String userEmail) {

        User user = userRepository.findByUserEmail(userEmail).orElseThrow(
            () -> new AppException(UserErrorCode.USER_NOT_FOUND)
        );

        if (user.isEmailVerified()) {
            throw new AppException(UserErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        Optional<EmailVerificationToken> existingToken =
            emailVerificationTokenRepository.findByUserEmail(userEmail);

        if (existingToken.isPresent()) {
            LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
            if (existingToken.get().getCreatedAt().isAfter(oneMinuteAgo)) {
                throw new AppException(UserErrorCode.VERIFICATION_CODE_RESEND_TOO_SOON);
            }
        }

        sendVerificationEmail(userEmail);
    }
}
