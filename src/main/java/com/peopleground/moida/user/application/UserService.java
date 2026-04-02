package com.peopleground.moida.user.application;

import com.peopleground.moida.global.dto.PageResponse;
import com.peopleground.moida.user.domain.repository.UserRepository;
import com.peopleground.moida.user.presentation.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsers(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> result = userRepository.findAllUsers(pageable).map(UserResponse::from);

        return PageResponse.from(result);
    }
}
