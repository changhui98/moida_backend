package com.peopleground.moida.like.domain.repository;

import com.peopleground.moida.like.domain.entity.ContentLike;
import java.util.Optional;

public interface ContentLikeRepository {

    ContentLike save(ContentLike contentLike);

    Optional<ContentLike> findByContentIdAndUserId(Long contentId, java.util.UUID userId);

    boolean existsByContentIdAndUserId(Long contentId, java.util.UUID userId);

    void delete(ContentLike contentLike);
}
