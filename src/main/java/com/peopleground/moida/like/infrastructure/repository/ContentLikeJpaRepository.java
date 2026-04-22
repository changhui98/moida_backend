package com.peopleground.moida.like.infrastructure.repository;

import com.peopleground.moida.like.domain.entity.ContentLike;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentLikeJpaRepository extends JpaRepository<ContentLike, Long> {

    Optional<ContentLike> findByContentIdAndUserId(Long contentId, UUID userId);

    boolean existsByContentIdAndUserId(Long contentId, UUID userId);
}
