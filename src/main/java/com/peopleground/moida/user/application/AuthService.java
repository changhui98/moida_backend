package com.peopleground.moida.user.application;

import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.repository.UserRepository;
import com.peopleground.moida.user.infrastructure.GeocodingClient;
import com.peopleground.moida.user.infrastructure.GeocodingClient.GeoPoint;
import com.peopleground.moida.user.presentation.dto.request.UserCreateRequest;
import com.peopleground.moida.user.presentation.dto.response.UserCreateResponse;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeocodingClient geocodingClient;
    private final EmailVerificationService emailVerificationService;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Transactional
    public UserCreateResponse signUp(UserCreateRequest request) {

        validateDuplicateUsername(request.username());
        validateDuplicateEmail(request.userEmail());

        Point point;

        GeoPoint geo = geocodingClient.convert(request.address());

        point = geometryFactory.createPoint(
            new Coordinate(geo.longitude(), geo.latitude())
        );
        point.setSRID(4326);

        User user = User.of(
            request.username(),
            passwordEncoder.encode(request.password()),
            request.nickname(),
            request.userEmail(),
            request.address(),
            point
        );

        User saveUser = userRepository.save(user);

        emailVerificationService.sendVerificationEmail(saveUser.getUserEmail());

        return UserCreateResponse.from(saveUser);
    }

    private void validateDuplicateEmail(String userEmail) {

        if (userRepository.existsByUserEmail(userEmail)) {
            throw new AppException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateDuplicateUsername(String username) {

        if (userRepository.existsByUsername(username)) {
            throw new AppException(UserErrorCode.DUPLICATE_USERNAME);
        }
    }
}
