package com.peopleground.moida.comment.application.service;

import com.peopleground.moida.comment.domain.CommentErrorCode;
import com.peopleground.moida.comment.domain.entity.Comment;
import com.peopleground.moida.comment.domain.repository.CommentRepository;
import com.peopleground.moida.comment.presentation.dto.request.CommentCreateRequest;
import com.peopleground.moida.comment.presentation.dto.request.CommentUpdateRequest;
import com.peopleground.moida.comment.presentation.dto.response.CommentListResponse;
import com.peopleground.moida.comment.presentation.dto.response.CommentResponse;
import com.peopleground.moida.content.domain.ContentErrorCode;
import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.content.domain.repository.ContentRepository;
import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.entity.UserRole;
import com.peopleground.moida.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    /**
     * 게시글의 댓글 목록을 커서 기반 페이지네이션으로 조회한다.
     * 각 최상위 댓글에 대댓글 목록을 함께 반환한다.
     */
    @Transactional(readOnly = true)
    public CommentListResponse getComments(Long contentId, Long cursorId, int size) {
        getActiveContent(contentId);

        List<Comment> topComments = commentRepository.findTopCommentsByContentId(contentId, cursorId, size);

        List<CommentResponse> commentResponses = topComments.stream()
            .map(comment -> {
                List<CommentResponse> replies = commentRepository.findRepliesByParentId(comment.getId())
                    .stream()
                    .map(CommentResponse::from)
                    .toList();
                return CommentResponse.from(comment, replies);
            })
            .toList();

        return CommentListResponse.of(commentResponses, size);
    }

    /**
     * 댓글을 작성한다. 게시글의 commentCount를 증가시킨다.
     */
    @Transactional
    public CommentResponse createComment(Long contentId, CommentCreateRequest req, CustomUser customUser) {
        Content content = getActiveContent(contentId);
        User author = getUser(customUser);

        Comment comment = commentRepository.save(Comment.of(content, author, req.body()));
        content.incrementCommentCount();

        return CommentResponse.from(comment);
    }

    /**
     * 대댓글을 작성한다. 부모 댓글이 이미 대댓글이면 예외를 발생시킨다.
     * 대댓글 작성도 게시글의 commentCount를 증가시킨다.
     */
    @Transactional
    public CommentResponse createReply(Long contentId, Long parentCommentId, CommentCreateRequest req, CustomUser customUser) {
        Content content = getActiveContent(contentId);
        User author = getUser(customUser);

        Comment parentComment = commentRepository.findById(parentCommentId)
            .orElseThrow(() -> new AppException(CommentErrorCode.COMMENT_NOT_FOUND));

        if (parentComment.isDeleted()) {
            throw new AppException(CommentErrorCode.COMMENT_ALREADY_DELETED);
        }

        // 대댓글에는 답글을 달 수 없다 (1 depth 제한)
        if (parentComment.isReply()) {
            throw new AppException(CommentErrorCode.COMMENT_REPLY_NOT_ALLOWED);
        }

        Comment reply = commentRepository.save(Comment.ofReply(content, parentComment, author, req.body()));
        content.incrementCommentCount();

        return CommentResponse.from(reply);
    }

    /**
     * 댓글을 수정한다. 본인만 수정 가능하다.
     */
    @Transactional
    public CommentResponse updateComment(Long contentId, Long commentId, CommentUpdateRequest req, CustomUser customUser) {
        getActiveContent(contentId);

        Comment comment = getActiveComment(commentId);
        validateCommentOwner(comment, customUser);

        comment.update(req.body());

        return CommentResponse.from(comment);
    }

    /**
     * 댓글을 소프트 삭제한다. 본인 또는 관리자만 삭제 가능하다.
     * 게시글의 commentCount를 감소시킨다.
     */
    @Transactional
    public void deleteComment(Long contentId, Long commentId, CustomUser customUser) {
        Content content = getActiveContent(contentId);

        Comment comment = getActiveComment(commentId);
        validateCommentOwnerOrAdmin(comment, customUser);

        // AuditingEntity의 deleteBy 활용: 소프트 삭제 패턴 준수
        User user = getUser(customUser);
        comment.deleteBy(user);
        content.decrementCommentCount();
    }

    private Content getActiveContent(Long contentId) {
        return contentRepository.findById(contentId)
            .orElseThrow(() -> new AppException(ContentErrorCode.CONTENT_NOT_FOUND));
    }

    private Comment getActiveComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new AppException(CommentErrorCode.COMMENT_NOT_FOUND));

        if (comment.isDeleted()) {
            throw new AppException(CommentErrorCode.COMMENT_ALREADY_DELETED);
        }

        return comment;
    }

    private void validateCommentOwner(Comment comment, CustomUser customUser) {
        if (!comment.getAuthor().getUsername().equals(customUser.getUsername())) {
            throw new AppException(CommentErrorCode.COMMENT_FORBIDDEN);
        }
    }

    private void validateCommentOwnerOrAdmin(Comment comment, CustomUser customUser) {
        boolean isOwner = comment.getAuthor().getUsername().equals(customUser.getUsername());
        boolean isAdmin = customUser.getRole() == UserRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AppException(CommentErrorCode.COMMENT_FORBIDDEN);
        }
    }

    private User getUser(CustomUser customUser) {
        return userRepository.findByUsername(customUser.getUsername())
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    }
}
