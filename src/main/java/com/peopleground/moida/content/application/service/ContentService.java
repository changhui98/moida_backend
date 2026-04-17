package com.peopleground.moida.content.application.service;

import com.peopleground.moida.content.domain.ContentErrorCode;
import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.content.domain.repository.ContentRepository;
import com.peopleground.moida.content.presentation.dto.request.ContentCreateRequest;
import com.peopleground.moida.content.presentation.dto.request.ContentUpdateRequest;
import com.peopleground.moida.content.presentation.dto.request.SearchType;
import com.peopleground.moida.content.presentation.dto.response.ContentCreateResponse;
import com.peopleground.moida.content.presentation.dto.response.ContentResponse;
import com.peopleground.moida.content.presentation.dto.response.ContentUpdateResponse;
import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.global.dto.PageResponse;
import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public PageResponse<ContentResponse> getContents(int page, int size, String keyword, SearchType searchType) {
        Pageable pageable = PageRequest.of(page, size);

        if (keyword != null && !keyword.isBlank()) {
            return PageResponse.from(
                contentRepository.searchContents(keyword, searchType, pageable).map(ContentResponse::from)
            );
        }

        return PageResponse.from(contentRepository.findAllContents(pageable).map(ContentResponse::from));
    }

    @Transactional
    public ContentUpdateResponse updateContent(Long contentId, ContentUpdateRequest req, CustomUser customUser) {

        Content content = getContentByOwner(contentId, customUser);
        content.update(req.title(), req.body());

        return ContentUpdateResponse.from(content);
    }

    @Transactional
    public void deleteContent(Long contentId, CustomUser customUser) {

        Content content = getContentByOwner(contentId, customUser);
        content.delete();
    }

    private Content getContentByOwner(Long contentId, CustomUser customUser) {

        Content content = contentRepository.findById(contentId)
            .orElseThrow(() -> new AppException(ContentErrorCode.CONTENT_NOT_FOUND));

        if (!content.getUser().getUsername().equals(customUser.getUsername())) {
            throw new AppException(ContentErrorCode.CONTENT_FORBIDDEN);
        }

        return content;
    }

    private User getUser(CustomUser user) {

        return userRepository.findByUsername(user.getUsername())
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    }
}
