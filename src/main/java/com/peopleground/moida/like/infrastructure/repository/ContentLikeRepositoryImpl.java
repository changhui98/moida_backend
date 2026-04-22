package com.peopleground.moida.like.infrastructure.repository;

import com.peopleground.moida.like.domain.entity.ContentLike;
import com.peopleground.moida.like.domain.repository.ContentLikeRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentLikeRepositoryImpl implements ContentLikeRepository {

    private final ContentLikeJpaRepository contentLikeJpaRepository;

    @Override
    public ContentLike save(ContentLike contentLike) {
        return contentLikeJpaRepository.save(contentLike);
    }

    @Override
    public Optional<ContentLike> findByContentIdAndUserId(Long contentId, UUID userId) {
        return contentLikeJpaRepository.findByContentIdAndUserId(contentId, userId);
    }

    @Override
    public boolean existsByContentIdAndUserId(Long contentId, UUID userId) {
        return contentLikeJpaRepository.existsByContentIdAndUserId(contentId, userId);
    }

    @Override
    public void delete(ContentLike contentLike) {
        contentLikeJpaRepository.delete(contentLike);
    }
}
