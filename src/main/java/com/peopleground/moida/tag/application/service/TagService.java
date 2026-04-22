package com.peopleground.moida.tag.application.service;

import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.content.domain.repository.ContentRepository;
import com.peopleground.moida.content.presentation.dto.response.ContentResponse;
import com.peopleground.moida.global.dto.PageResponse;
import com.peopleground.moida.tag.domain.TagErrorCode;
import com.peopleground.moida.tag.domain.entity.ContentTag;
import com.peopleground.moida.tag.domain.entity.Tag;
import com.peopleground.moida.tag.domain.repository.ContentTagRepository;
import com.peopleground.moida.tag.domain.repository.TagRepository;
import com.peopleground.moida.tag.presentation.dto.response.TagResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

    private static final int POPULAR_TAGS_LIMIT = 20;
    private static final int AUTOCOMPLETE_LIMIT = 10;
    private static final int MAX_TAGS_PER_CONTENT = 10;
    private static final int MAX_TAG_LENGTH = 30;

    private final TagRepository tagRepository;
    private final ContentTagRepository contentTagRepository;
    private final ContentRepository contentRepository;

    /**
     * 인기 태그 목록을 조회한다. (Cache-Aside, TTL 10분은 Redis 설정에서 관리)
     */
    @Cacheable(value = "popularTags", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    public List<TagResponse> getPopularTags() {
        return tagRepository.findTopByPostCount(POPULAR_TAGS_LIMIT)
            .stream()
            .map(TagResponse::from)
            .toList();
    }

    /**
     * 태그 자동완성 검색. 입력된 키워드로 태그명을 부분 검색하여 반환한다.
     * (Cache-Aside, TTL 30분은 Redis 설정에서 관리)
     */
    @Cacheable(value = "tagAutocomplete", key = "#keyword")
    @Transactional(readOnly = true)
    public List<TagResponse> searchTags(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return tagRepository.searchByNameContaining(keyword, AUTOCOMPLETE_LIMIT)
            .stream()
            .map(TagResponse::from)
            .toList();
    }

    /**
     * 특정 태그의 게시글 목록을 조회한다.
     */
    @Transactional(readOnly = true)
    public PageResponse<ContentResponse> getContentsByTagName(String tagName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageResponse.from(
            contentRepository.findAllByTagName(tagName, pageable).map(ContentResponse::from)
        );
    }

    /**
     * 게시글에 태그를 추가한다.
     * 기존에 없는 태그는 자동 생성하고, 이미 있는 태그는 조회하여 연결한다.
     * Tag.postCount 비정규화 필드를 함께 갱신한다.
     */
    @CacheEvict(value = {"popularTags", "tagAutocomplete"}, allEntries = true)
    @Transactional
    public void attachTagsToContent(Content content, List<String> tagNames) {
        validateTagNames(tagNames);

        for (String tagName : tagNames) {
            String normalizedName = tagName.toLowerCase().trim();
            Tag tag = tagRepository.findByName(normalizedName)
                .orElseGet(() -> tagRepository.save(Tag.of(normalizedName)));

            contentTagRepository.save(ContentTag.of(content, tag));
            tag.incrementPostCount();
        }
    }

    /**
     * 게시글 태그를 모두 제거하고 새 태그 목록으로 교체한다.
     * 기존 연결 태그의 postCount를 감소시킨 뒤 새 태그를 연결한다.
     */
    @CacheEvict(value = {"popularTags", "tagAutocomplete"}, allEntries = true)
    @Transactional
    public void updateContentTags(Content content, List<String> newTagNames) {
        // 기존 태그 연결 제거 및 postCount 감소
        List<ContentTag> existingTags = contentTagRepository.findAllByContent(content);
        for (ContentTag contentTag : existingTags) {
            contentTag.getTag().decrementPostCount();
        }
        contentTagRepository.deleteAllByContent(content);

        // 새 태그 연결
        if (newTagNames != null && !newTagNames.isEmpty()) {
            attachTagsToContent(content, newTagNames);
        }
    }

    /**
     * 게시글 소프트 삭제 시 태그 연결을 제거하고 postCount를 감소시킨다.
     */
    @CacheEvict(value = {"popularTags", "tagAutocomplete"}, allEntries = true)
    @Transactional
    public void detachTagsFromContent(Content content) {
        List<ContentTag> existingTags = contentTagRepository.findAllByContent(content);
        for (ContentTag contentTag : existingTags) {
            contentTag.getTag().decrementPostCount();
        }
        contentTagRepository.deleteAllByContent(content);
    }

    /**
     * 특정 게시글에 연결된 태그 이름 목록을 반환한다.
     */
    @Transactional(readOnly = true)
    public List<String> getTagNamesByContent(Content content) {
        return contentTagRepository.findAllByContent(content)
            .stream()
            .map(ct -> ct.getTag().getName())
            .toList();
    }

    private void validateTagNames(List<String> tagNames) {
        if (tagNames == null) {
            return;
        }
        if (tagNames.size() > MAX_TAGS_PER_CONTENT) {
            throw new com.peopleground.moida.global.exception.AppException(TagErrorCode.TAG_LIMIT_EXCEEDED);
        }
        for (String name : tagNames) {
            if (name != null && name.length() > MAX_TAG_LENGTH) {
                throw new com.peopleground.moida.global.exception.AppException(TagErrorCode.TAG_NAME_TOO_LONG);
            }
        }
    }
}
