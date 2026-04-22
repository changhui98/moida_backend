package com.peopleground.moida.comment.infrastructure.repository;

import com.peopleground.moida.comment.domain.entity.Comment;
import com.peopleground.moida.comment.domain.entity.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 게시글의 최상위 댓글을 커서 기반 페이지네이션으로 조회한다.
     * 소프트 삭제된 댓글도 포함하되(대댓글 구조 유지), body는 응답 DTO에서 치환한다.
     * createdDate 오름차순(오래된 순) 정렬.
     */
    public List<Comment> findTopCommentsByContentId(Long contentId, Long cursorId, int size) {
        QComment comment = QComment.comment;

        var query = queryFactory
            .selectFrom(comment)
            .where(
                comment.content.id.eq(contentId),
                comment.parent.isNull()
            );

        if (cursorId != null) {
            query = query.where(comment.id.gt(cursorId));
        }

        return query
            .orderBy(comment.createdDate.asc())
            .limit(size)
            .fetch();
    }

    /**
     * 특정 댓글의 대댓글 목록 전체를 조회한다. (createdDate 오름차순)
     * 소프트 삭제된 대댓글도 포함한다.
     */
    public List<Comment> findRepliesByParentId(Long parentId) {
        QComment comment = QComment.comment;

        return queryFactory
            .selectFrom(comment)
            .where(comment.parent.id.eq(parentId))
            .orderBy(comment.createdDate.asc())
            .fetch();
    }

    /**
     * 게시글의 삭제되지 않은 댓글 수를 반환한다. (commentCount 동기화용)
     */
    public int countByContentId(Long contentId) {
        QComment comment = QComment.comment;

        Long count = queryFactory
            .select(comment.count())
            .from(comment)
            .where(
                comment.content.id.eq(contentId),
                comment.deletedDate.isNull()
            )
            .fetchOne();

        return count != null ? count.intValue() : 0;
    }
}
