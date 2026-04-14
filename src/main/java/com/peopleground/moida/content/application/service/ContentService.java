package com.peopleground.moida.content.application.service;

import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.content.domain.repository.ContentRepository;
import com.peopleground.moida.content.presentation.dto.request.ContentCreateRequest;
import com.peopleground.moida.content.presentation.dto.response.ContentCreateResponse;
import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    @Transactional
    public ContentCreateResponse contentCreate(ContentCreateRequest req, CustomUser user) {

        User findUser = getUser(user);

        return ContentCreateResponse.from(contentRepository.save(Content.of(req.title(), req.body(), findUser)));
    }

    private User getUser(CustomUser user) {

        return  userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    }
}
