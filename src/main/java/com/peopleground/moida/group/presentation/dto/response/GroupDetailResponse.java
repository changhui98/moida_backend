package com.peopleground.moida.group.presentation.dto.response;

import com.peopleground.moida.group.domain.entity.Group;
import com.peopleground.moida.group.domain.entity.GroupCategory;
import java.time.LocalDateTime;
import java.util.List;

public record GroupDetailResponse(
    Long id,
    String name,
    String description,
    GroupCategory category,
    int maxMemberCount,
    int currentMemberCount,
    String leaderNickname,
    String leaderUsername,
    LocalDateTime createdDate,
    List<GroupMemberResponse> members
) {

    public static GroupDetailResponse of(Group group, List<GroupMemberResponse> members) {
        return new GroupDetailResponse(
            group.getId(),
            group.getName(),
            group.getDescription(),
            group.getCategory(),
            group.getMaxMemberCount(),
            group.getCurrentMemberCount(),
            group.getLeader().getNickname(),
            group.getLeader().getUsername(),
            group.getCreatedDate(),
            members
        );
    }
}
