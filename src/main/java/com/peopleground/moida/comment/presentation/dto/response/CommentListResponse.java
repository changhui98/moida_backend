package com.peopleground.moida.comment.presentation.dto.response;

import java.util.List;

public record CommentListResponse(
    List<CommentResponse> comments,
    Long nextCursorId,
    boolean hasNext
) {
    public static CommentListResponse of(List<CommentResponse> comments, int requestedSize) {
        boolean hasNext = comments.size() == requestedSize;
        Long nextCursorId = hasNext && !comments.isEmpty()
            ? comments.getLast().id()
            : null;
        return new CommentListResponse(comments, nextCursorId, hasNext);
    }
}
