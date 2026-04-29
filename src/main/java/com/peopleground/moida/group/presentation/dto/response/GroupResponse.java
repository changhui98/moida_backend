package com.peopleground.moida.group.presentation.dto.response;

import com.peopleground.moida.group.domain.entity.Group;
import com.peopleground.moida.group.domain.entity.GroupCategory;
import java.time.LocalDateTime;

public record GroupResponse(
    Long id,
    String name,
    String description,
    GroupCategory category,
    int maxMemberCount,
    int currentMemberCount,
    String leaderNickname,
    String leaderUsername,
    LocalDateTime createdDate,
    String imageUrl,
    int likeCount
) {

    public static GroupResponse from(Group group) {
        return new GroupResponse(
            group.getId(),
            group.getName(),
            group.getDescription(),
            group.getCategory(),
            group.getMaxMemberCount(),
            group.getCurrentMemberCount(),
            group.getLeader().getNickname(),
            group.getLeader().getUsername(),
            group.getCreatedDate(),
            group.getImageUrl(),
            group.getLikeCount()
        );
    }
}
