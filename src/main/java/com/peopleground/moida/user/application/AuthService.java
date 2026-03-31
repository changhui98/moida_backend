package com.peopleground.moida.user.application;

import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.repository.UserRepository;
import com.peopleground.moida.user.presentation.dto.request.UserCreateRequest;
import com.peopleground.moida.user.presentation.dto.response.UserCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCreateResponse signUp(UserCreateRequest request) {

        validateDuplicateUsername(request.username());

        User user = User.of(
            request.username(),
            passwordEncoder.encode(request.password())
        );

        User saveUser = userRepository.save(user);

        return UserCreateResponse.from(saveUser);
    }

    private void validateDuplicateUsername(String username) {

        if (userRepository.existsByUsername(username)) {
            throw new AppException(UserErrorCode.DUPLICATE_USERNAME);
        }
    }
}
