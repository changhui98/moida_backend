package com.peopleground.moida.like.presentation.dto.response;

public record LikeStatusResponse(
    boolean liked
) {
    public static LikeStatusResponse of(boolean liked) {
        return new LikeStatusResponse(liked);
    }
}
