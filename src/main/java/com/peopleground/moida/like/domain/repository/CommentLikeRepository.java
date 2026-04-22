package com.peopleground.moida.like.domain.repository;

import com.peopleground.moida.like.domain.entity.CommentLike;
import java.util.Optional;
import java.util.UUID;

public interface CommentLikeRepository {

    CommentLike save(CommentLike commentLike);

    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, UUID userId);

    boolean existsByCommentIdAndUserId(Long commentId, UUID userId);

    void delete(CommentLike commentLike);
}
