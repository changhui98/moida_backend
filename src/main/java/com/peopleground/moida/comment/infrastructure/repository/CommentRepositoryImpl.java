package com.peopleground.moida.comment.infrastructure.repository;

import com.peopleground.moida.comment.domain.entity.Comment;
import com.peopleground.moida.comment.domain.repository.CommentRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;
    private final CommentQueryRepository commentQueryRepository;

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentJpaRepository.findById(id);
    }

    @Override
    public List<Comment> findTopCommentsByContentId(Long contentId, Long cursorId, int size) {
        return commentQueryRepository.findTopCommentsByContentId(contentId, cursorId, size);
    }

    @Override
    public List<Comment> findRepliesByParentId(Long parentId) {
        return commentQueryRepository.findRepliesByParentId(parentId);
    }

    @Override
    public int countByContentId(Long contentId) {
        return commentQueryRepository.countByContentId(contentId);
    }
}
