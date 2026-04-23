package com.peopleground.moida.user.application;

import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.global.dto.PageResponse;
import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.repository.UserRepository;
import com.peopleground.moida.user.infrastructure.GeocodingClient;
import com.peopleground.moida.user.infrastructure.GeocodingClient.GeoPoint;
import com.peopleground.moida.user.presentation.dto.request.UserUpdateRequest;
import com.peopleground.moida.user.presentation.dto.response.UserDetailResponse;
import com.peopleground.moida.user.presentation.dto.response.UserResponse;
import com.peopleground.moida.user.presentation.dto.response.UserResponseMarker;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeocodingClient geocodingClient;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Transactional(readOnly = true)
    public UserDetailResponse getMyProfile(CustomUser customUser) {
        User user = getUser(customUser);

        return UserDetailResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getProfileByUsername(String username) {
        User user = getActiveUserByUsername(username);
        return UserDetailResponse.from(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponseMarker> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<UserResponse> result = userRepository.findAllUsers(pageable).map(UserResponse::from);

        return PageResponse.from(result);
    }

    @Transactional
    public UserDetailResponse updateProfile(CustomUser customUser, UserUpdateRequest req) {

        User user = getUser(customUser);

        String encodedNewPassword = user.getPassword();
        if (req.newPassword() != null && !req.newPassword().isBlank()) {

            if (req.currentPassword() == null || req.currentPassword().isBlank()) {
                throw new AppException(UserErrorCode.PASSWORD_REQUIRED);
            }

            if (!passwordEncoder.matches(req.currentPassword(), user.getPassword())) {
                throw new AppException(UserErrorCode.INVALID_CURRENT_PASSWORD);
            }
            encodedNewPassword = passwordEncoder.encode(req.newPassword());
        }

        String address = req.address() != null ? req.address() : user.getAddress();
        Point location;

        if (req.address() != null) {
            GeoPoint geo = geocodingClient.convert(req.address());
            location = geometryFactory.createPoint(
                new Coordinate(geo.longitude(), geo.latitude())
            );
            location.setSRID(4326);
        } else {
            location = user.getLocation();
        }

        String nickname = req.nickname() != null ? req.nickname() : user.getNickname();
        String userEmail = req.userEmail() != null ? req.userEmail() : user.getUserEmail();

        User updateUser = user.updateUser(nickname, userEmail, address, location, encodedNewPassword);

        User saveUser = userRepository.updateProfile(updateUser);

        return  UserDetailResponse.from(saveUser);
    }

    @Transactional
    public void deleteUser(CustomUser customUser) {
        User user = getUser(customUser);

        user.delete();
        userRepository.save(user);
    }

    private User getUser(CustomUser customUser) {
        return getActiveUserByUsername(customUser.getUsername());
    }

    private User getActiveUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
            () -> new AppException(UserErrorCode.USER_NOT_FOUND)
        );

        if (user.isDeleted()) {
            throw new AppException(UserErrorCode.USER_NOT_FOUND);
        }

        return user;
    }
}
