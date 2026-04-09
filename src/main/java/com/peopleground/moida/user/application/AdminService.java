package com.peopleground.moida.user.application;

import com.peopleground.moida.global.dto.PageResponse;
import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.repository.UserRepository;
import com.peopleground.moida.user.presentation.dto.response.AdminUserDetailResponse;
import com.peopleground.moida.user.presentation.dto.response.AdminUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public PageResponse<AdminUserResponse> getUsersForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return PageResponse.from(
            userRepository.findAllUserForAdmin(pageable).map(AdminUserResponse::from)
        );
    }

    public AdminUserDetailResponse getUserForAdmin(String username) {

        return userRepository.findByUsername(username).map(AdminUserDetailResponse::from).orElseThrow(
            () -> new AppException(UserErrorCode.USER_NOT_FOUND)
        );
    }
}
