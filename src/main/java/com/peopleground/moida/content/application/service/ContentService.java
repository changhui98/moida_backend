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
import com.peopleground.moida.like.domain.repository.ContentLikeRepository;
import com.peopleground.moida.tag.application.service.TagService;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final TagService tagService;
    private final ContentLikeRepository contentLikeRepository;

    @CacheEvict(value = "contentList", allEntries = true)
    @Transactional
    public ContentCreateResponse contentCreate(ContentCreateRequest req, CustomUser user) {

        User findUser = getUser(user);
        Content content = contentRepository.save(Content.of(req.title(), req.body(), findUser));

        // 태그가 있는 경우 태그 연동 처리
        List<String> tags = req.tags();
        if (tags != null && !tags.isEmpty()) {
            tagService.attachTagsToContent(content, tags);
        }

        return ContentCreateResponse.from(content);
    }

    @Transactional(readOnly = true)
    public PageResponse<ContentResponse> getContents(
        int page, int size, String keyword, SearchType searchType, CustomUser user
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Content> contents = (keyword != null && !keyword.isBlank())
            ? contentRepository.searchContents(keyword, searchType, pageable)
            : contentRepository.findAllContents(pageable);

        return PageResponse.from(toResponsePage(contents, user));
    }

    /**
     * 현재 로그인한 사용자가 작성한 글 목록을 최신순으로 페이지네이션하여 반환한다.
     */
    @Transactional(readOnly = true)
    public PageResponse<ContentResponse> getMyContents(CustomUser customUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Content> contents = contentRepository.findAllByUsername(customUser.getUsername(), pageable);
        return PageResponse.from(toResponsePage(contents, customUser));
    }

    @CacheEvict(value = "contentList", allEntries = true)
    @Transactional
    public ContentUpdateResponse updateContent(Long contentId, ContentUpdateRequest req, CustomUser customUser) {

        Content content = getContentByOwner(contentId, customUser);
        content.update(req.title(), req.body());

        // tags 필드가 null이 아닌 경우 태그 교체 (null이면 태그 변경 없음)
        if (req.tags() != null) {
            tagService.updateContentTags(content, req.tags());
        }

        return ContentUpdateResponse.from(content);
    }

    @CacheEvict(value = "contentList", allEntries = true)
    @Transactional
    public void deleteContent(Long contentId, CustomUser customUser) {

        Content content = getContentByOwner(contentId, customUser);
        // 게시글 소프트 삭제 시 태그 연결 데이터 정리
        tagService.detachTagsFromContent(content);
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

    /**
     * Page&lt;Content&gt; 를 현재 사용자의 좋아요 여부(likedByMe) 까지 채워진
     * Page&lt;ContentResponse&gt; 로 변환한다.
     *
     * <p>성능 노트</p>
     * <ul>
     *   <li>현재 페이지의 모든 contentId 를 한 번에 넘겨 배치 조회 → N+1 방지.</li>
     *   <li>로그인하지 않았거나 페이지가 비어 있으면 DB 쿼리를 스킵하고 likedByMe=false 로 고정.</li>
     * </ul>
     */
    private Page<ContentResponse> toResponsePage(Page<Content> contents, CustomUser user) {
        if (contents.isEmpty() || user == null) {
            return contents.map(ContentResponse::from);
        }

        List<Long> ids = contents.getContent().stream().map(Content::getId).toList();
        UUID userId = user.getId();
        Set<Long> likedIds = contentLikeRepository.findLikedContentIds(userId, ids);

        return contents.map(c -> ContentResponse.from(c, likedIds.contains(c.getId())));
    }
}
