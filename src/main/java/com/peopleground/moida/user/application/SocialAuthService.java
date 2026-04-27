package com.peopleground.moida.user.application;

import com.peopleground.moida.global.exception.ApiErrorCode;
import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.global.security.jwt.JwtTokenProvider;
import com.peopleground.moida.user.domain.entity.OAuthProvider;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.repository.UserRepository;
import com.peopleground.moida.user.infrastructure.oauth.GoogleOAuthClient;
import com.peopleground.moida.user.infrastructure.oauth.GoogleUserProfile;
import com.peopleground.moida.user.infrastructure.oauth.KakaoOAuthClient;
import com.peopleground.moida.user.infrastructure.oauth.KakaoUserProfile;
import com.peopleground.moida.user.presentation.dto.request.SocialSignInRequest;
import com.peopleground.moida.user.presentation.dto.response.SocialSignInResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialAuthService {

    private final UserRepository userRepository;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final GoogleOAuthClient googleOAuthClient;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SocialSignInResponse socialSignIn(SocialSignInRequest request) {
        OAuthProvider provider = resolveProvider(request.provider());

        return switch (provider) {
            case KAKAO -> handleKakaoSignIn(request.code(), request.redirectUri());
            case GOOGLE -> handleGoogleSignIn(request.code(), request.redirectUri());
            default -> throw new AppException(ApiErrorCode.INVALID_REQUEST);
        };
    }

    private SocialSignInResponse handleKakaoSignIn(String code, String redirectUri) {
        String accessToken = kakaoOAuthClient.exchangeToken(code, redirectUri);
        KakaoUserProfile profile = kakaoOAuthClient.getUserProfile(accessToken);

        String providerId = String.valueOf(profile.id());
        Optional<User> existing = userRepository.findByProviderAndProviderId(OAuthProvider.KAKAO, providerId);

        if (existing.isPresent()) {
            User user = existing.get();
            String jwtToken = jwtTokenProvider.createToken(user.getId(), user.getUsername(), user.getRole());
            return new SocialSignInResponse(jwtToken, false, user.getNickname());
        }

        // 신규 가입
        String email = profile.email() != null ? profile.email()
            : OAuthProvider.KAKAO.name().toLowerCase() + "_" + providerId + "@moida.social";

        User newUser = User.ofSocial(
            OAuthProvider.KAKAO,
            providerId,
            profile.nickname(),
            email,
            profile.profileImageUrl(),
            ""
        );
        User saved = userRepository.save(newUser);
        String jwtToken = jwtTokenProvider.createToken(saved.getId(), saved.getUsername(), saved.getRole());
        return new SocialSignInResponse(jwtToken, true, saved.getNickname());
    }

    private SocialSignInResponse handleGoogleSignIn(String code, String redirectUri) {
        String accessToken = googleOAuthClient.exchangeToken(code, redirectUri);
        GoogleUserProfile profile = googleOAuthClient.getUserProfile(accessToken);

        String providerId = profile.sub();
        Optional<User> existing = userRepository.findByProviderAndProviderId(OAuthProvider.GOOGLE, providerId);

        if (existing.isPresent()) {
            User user = existing.get();
            String jwtToken = jwtTokenProvider.createToken(user.getId(), user.getUsername(), user.getRole());
            return new SocialSignInResponse(jwtToken, false, user.getNickname());
        }

        // 신규 가입
        String email = profile.email() != null ? profile.email()
            : OAuthProvider.GOOGLE.name().toLowerCase() + "_" + providerId + "@moida.social";

        User newUser = User.ofSocial(
            OAuthProvider.GOOGLE,
            providerId,
            profile.name() != null ? profile.name() : "구글사용자",
            email,
            profile.picture(),
            ""
        );
        User saved = userRepository.save(newUser);
        String jwtToken = jwtTokenProvider.createToken(saved.getId(), saved.getUsername(), saved.getRole());
        return new SocialSignInResponse(jwtToken, true, saved.getNickname());
    }

    private OAuthProvider resolveProvider(String provider) {
        try {
            return OAuthProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ApiErrorCode.INVALID_REQUEST);
        }
    }
}
