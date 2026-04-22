package com.peopleground.moida.comment.domain.repository;

import com.peopleground.moida.comment.domain.entity.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Comment save(Comment comment);

    Optional<Comment> findById(Long id);

    /**
     * 게시글의 최상위 댓글 목록을 커서 기반으로 조회한다. (createdDate 오름차순)
     * cursorId가 null이면 첫 페이지를 반환한다.
     */
    List<Comment> findTopCommentsByContentId(Long contentId, Long cursorId, int size);

    /**
     * 특정 댓글의 대댓글 목록을 조회한다. (createdDate 오름차순)
     */
    List<Comment> findRepliesByParentId(Long parentId);

    int countByContentId(Long contentId);
}
