package com.peopleground.moida.like.presentation.dto.response;

public record LikeToggleResponse(
    boolean liked,
    int likeCount
) {
    public static LikeToggleResponse liked(int likeCount) {
        return new LikeToggleResponse(true, likeCount);
    }

    public static LikeToggleResponse unliked(int likeCount) {
        return new LikeToggleResponse(false, likeCount);
    }
}
