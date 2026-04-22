package com.peopleground.moida.like.infrastructure.repository;

import com.peopleground.moida.like.domain.entity.CommentLike;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeJpaRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, UUID userId);

    boolean existsByCommentIdAndUserId(Long commentId, UUID userId);
}
