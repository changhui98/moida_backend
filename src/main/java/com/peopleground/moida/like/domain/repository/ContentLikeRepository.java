package com.peopleground.moida.like.domain.repository;

import com.peopleground.moida.like.domain.entity.ContentLike;
import java.util.Optional;
import java.util.UUID;

public interface ContentLikeRepository {

    ContentLike save(ContentLike contentLike);

    Optional<ContentLike> findByContentIdAndUserId(Long contentId, UUID userId);

    boolean existsByContentIdAndUserId(Long contentId, UUID userId);

    void delete(ContentLike contentLike);

    /**
     * 동시성-안전 좋아요 삽입. UNIQUE (content_id, user_id) 경합이 발생해도
     * 예외 없이 실제 삽입된 행 수(0 또는 1)를 반환한다.
     */
    int insertIfNotExists(Long contentId, UUID userId);
}
