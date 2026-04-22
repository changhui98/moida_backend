package com.peopleground.moida.like.application.service;

import com.peopleground.moida.comment.domain.CommentErrorCode;
import com.peopleground.moida.comment.domain.entity.Comment;
import com.peopleground.moida.comment.domain.repository.CommentRepository;
import com.peopleground.moida.content.domain.ContentErrorCode;
import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.content.domain.repository.ContentRepository;
import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.like.domain.entity.CommentLike;
import com.peopleground.moida.like.domain.entity.ContentLike;
import com.peopleground.moida.like.domain.repository.CommentLikeRepository;
import com.peopleground.moida.like.domain.repository.ContentLikeRepository;
import com.peopleground.moida.like.presentation.dto.response.LikeStatusResponse;
import com.peopleground.moida.like.presentation.dto.response.LikeToggleResponse;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final ContentLikeRepository contentLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ContentRepository contentRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 좋아요 토글.
     * 이미 좋아요가 있으면 취소, 없으면 추가한다.
     * Content.likeCount 비정규화 필드를 함께 갱신한다.
     * 좋아요 변경 시 캐시를 무효화한다.
     */
    @CacheEvict(value = "contentLikeCount", key = "#contentId")
    @Transactional
    public LikeToggleResponse toggleContentLike(Long contentId, CustomUser customUser) {
        Content content = contentRepository.findById(contentId)
            .orElseThrow(() -> new AppException(ContentErrorCode.CONTENT_NOT_FOUND));

        User user = getUser(customUser);

        Optional<ContentLike> existingLike = contentLikeRepository.findByContentIdAndUserId(contentId, user.getId());

        if (existingLike.isPresent()) {
            // 좋아요 취소
            contentLikeRepository.delete(existingLike.get());
            content.decrementLikeCount();
            return LikeToggleResponse.unliked(content.getLikeCount());
        } else {
            // 좋아요 추가
            contentLikeRepository.save(ContentLike.of(content, user));
            content.incrementLikeCount();
            return LikeToggleResponse.liked(content.getLikeCount());
        }
    }

    /**
     * 댓글 좋아요 토글.
     * 이미 좋아요가 있으면 취소, 없으면 추가한다.
     * Comment.likeCount 비정규화 필드를 함께 갱신한다.
     */
    @CacheEvict(value = "commentLikeCount", key = "#commentId")
    @Transactional
    public LikeToggleResponse toggleCommentLike(Long commentId, CustomUser customUser) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new AppException(CommentErrorCode.COMMENT_NOT_FOUND));

        if (comment.isDeleted()) {
            throw new AppException(CommentErrorCode.COMMENT_ALREADY_DELETED);
        }

        User user = getUser(customUser);

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId());

        if (existingLike.isPresent()) {
            // 좋아요 취소
            commentLikeRepository.delete(existingLike.get());
            comment.decrementLikeCount();
            return LikeToggleResponse.unliked(comment.getLikeCount());
        } else {
            // 좋아요 추가
            commentLikeRepository.save(CommentLike.of(comment, user));
            comment.incrementLikeCount();
            return LikeToggleResponse.liked(comment.getLikeCount());
        }
    }

    /**
     * 내 게시글 좋아요 여부 확인
     */
    @Transactional(readOnly = true)
    public LikeStatusResponse getContentLikeStatus(Long contentId, CustomUser customUser) {
        User user = getUser(customUser);
        boolean liked = contentLikeRepository.existsByContentIdAndUserId(contentId, user.getId());
        return LikeStatusResponse.of(liked);
    }

    /**
     * 내 댓글 좋아요 여부 확인
     */
    @Transactional(readOnly = true)
    public LikeStatusResponse getCommentLikeStatus(Long commentId, CustomUser customUser) {
        User user = getUser(customUser);
        boolean liked = commentLikeRepository.existsByCommentIdAndUserId(commentId, user.getId());
        return LikeStatusResponse.of(liked);
    }

    private User getUser(CustomUser customUser) {
        return userRepository.findByUsername(customUser.getUsername())
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    }
}
