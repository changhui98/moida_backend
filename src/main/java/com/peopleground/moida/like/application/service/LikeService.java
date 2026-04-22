package com.peopleground.moida.like.application.service;

import com.peopleground.moida.comment.domain.CommentErrorCode;
import com.peopleground.moida.comment.domain.entity.Comment;
import com.peopleground.moida.comment.domain.repository.CommentRepository;
import com.peopleground.moida.content.domain.ContentErrorCode;
import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.content.domain.repository.ContentRepository;
import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.global.exception.AppException;
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
     *
     * <p>동시성 전략</p>
     * <ul>
     *   <li>좋아요 추가: <code>INSERT ... ON CONFLICT DO NOTHING</code> 으로 UNIQUE 경합을
     *       예외 없이 처리한다. 실제 삽입 행이 1 인 경우에만 likeCount 를 원자 UPDATE 로 증가시킨다.</li>
     *   <li>좋아요 취소: DELETE 후 원자 UPDATE 로 감소. 0 미만으로는 내려가지 않는다.</li>
     *   <li>다중 사용자 동시 좋아요에서도 likeCount 의 Lost Update 를 방지한다.</li>
     * </ul>
     */
    @CacheEvict(value = "contentLikeCount", key = "#contentId")
    @Transactional
    public LikeToggleResponse toggleContentLike(Long contentId, CustomUser customUser) {
        Content content = contentRepository.findById(contentId)
            .orElseThrow(() -> new AppException(ContentErrorCode.CONTENT_NOT_FOUND));

        User user = getUser(customUser);

        Optional<ContentLike> existingLike = contentLikeRepository.findByContentIdAndUserId(contentId, user.getId());

        if (existingLike.isPresent()) {
            contentLikeRepository.delete(existingLike.get());
            contentRepository.decrementLikeCount(contentId);
            return LikeToggleResponse.unliked(currentContentLikeCount(contentId));
        }

        int inserted = contentLikeRepository.insertIfNotExists(content.getId(), user.getId());
        if (inserted == 1) {
            contentRepository.incrementLikeCount(contentId);
        }
        return LikeToggleResponse.liked(currentContentLikeCount(contentId));
    }

    /**
     * 댓글 좋아요 토글. 전략은 게시글 토글과 동일하다. (ON CONFLICT DO NOTHING + 원자 UPDATE)
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

        boolean alreadyLiked = commentLikeRepository.existsByCommentIdAndUserId(commentId, user.getId());

        if (alreadyLiked) {
            commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId())
                .ifPresent(commentLikeRepository::delete);
            commentRepository.decrementLikeCount(commentId);
            return LikeToggleResponse.unliked(currentCommentLikeCount(commentId));
        }

        int inserted = commentLikeRepository.insertIfNotExists(commentId, user.getId());
        if (inserted == 1) {
            commentRepository.incrementLikeCount(commentId);
        }
        return LikeToggleResponse.liked(currentCommentLikeCount(commentId));
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

    private int currentContentLikeCount(Long contentId) {
        Integer value = contentRepository.findLikeCountById(contentId);
        return value != null ? value : 0;
    }

    private int currentCommentLikeCount(Long commentId) {
        Integer value = commentRepository.findLikeCountById(commentId);
        return value != null ? value : 0;
    }
}
