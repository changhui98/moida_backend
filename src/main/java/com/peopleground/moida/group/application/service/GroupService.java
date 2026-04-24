package com.peopleground.moida.group.application.service;

import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.global.dto.PageResponse;
import com.peopleground.moida.global.exception.AppException;
import com.peopleground.moida.group.domain.GroupErrorCode;
import com.peopleground.moida.group.domain.entity.Group;
import com.peopleground.moida.group.domain.entity.GroupCategory;
import com.peopleground.moida.group.domain.entity.GroupMember;
import com.peopleground.moida.group.domain.entity.GroupMemberRole;
import com.peopleground.moida.group.domain.repository.GroupMemberRepository;
import com.peopleground.moida.group.domain.repository.GroupRepository;
import com.peopleground.moida.group.presentation.dto.request.GroupCreateRequest;
import com.peopleground.moida.group.presentation.dto.request.GroupUpdateRequest;
import com.peopleground.moida.group.presentation.dto.response.GroupDetailResponse;
import com.peopleground.moida.group.presentation.dto.response.GroupMemberResponse;
import com.peopleground.moida.group.presentation.dto.response.GroupResponse;
import com.peopleground.moida.user.domain.UserErrorCode;
import com.peopleground.moida.user.domain.entity.User;
import com.peopleground.moida.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public GroupResponse createGroup(GroupCreateRequest request, CustomUser customUser) {
        User leader = getUser(customUser.getUsername());

        Group group = Group.of(
            request.name(),
            request.description(),
            request.category(),
            request.maxMemberCount(),
            leader
        );

        Group saved = groupRepository.save(group);

        // 생성자를 자동으로 LEADER로 등록
        GroupMember leaderMember = GroupMember.of(saved, leader, GroupMemberRole.LEADER);
        groupMemberRepository.save(leaderMember);
        saved.incrementMemberCount();
        groupRepository.save(saved);

        return GroupResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<GroupResponse> getGroups(int page, int size, String keyword, GroupCategory category) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Group> groups = groupRepository.findAll(pageable, keyword, category);
        return PageResponse.from(groups.map(GroupResponse::from));
    }

    @Transactional(readOnly = true)
    public GroupDetailResponse getGroup(Long groupId) {
        Group group = findGroup(groupId);
        List<GroupMemberResponse> members = groupMemberRepository.findByGroupId(groupId)
            .stream()
            .map(GroupMemberResponse::from)
            .toList();
        return GroupDetailResponse.of(group, members);
    }

    @Transactional
    public GroupResponse updateGroup(Long groupId, GroupUpdateRequest request, CustomUser customUser) {
        Group group = findGroup(groupId);
        validateLeader(group, customUser.getUsername());

        group.update(
            request.name(),
            request.description(),
            request.category(),
            request.maxMemberCount()
        );

        return GroupResponse.from(groupRepository.save(group));
    }

    @Transactional
    public void deleteGroup(Long groupId, CustomUser customUser) {
        Group group = findGroup(groupId);
        validateLeader(group, customUser.getUsername());

        // 소속 멤버 전체 하드 삭제
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        for (GroupMember member : members) {
            groupMemberRepository.delete(member);
        }

        User user = getUser(customUser.getUsername());
        group.delete(user);
        groupRepository.save(group);
    }

    @Transactional
    public void joinGroup(Long groupId, CustomUser customUser) {
        Group group = findGroup(groupId);

        if (groupMemberRepository.existsByGroupIdAndUsername(groupId, customUser.getUsername())) {
            throw new AppException(GroupErrorCode.GROUP_ALREADY_JOINED);
        }

        if (group.isFull()) {
            throw new AppException(GroupErrorCode.GROUP_FULL);
        }

        User user = getUser(customUser.getUsername());
        GroupMember member = GroupMember.of(group, user, GroupMemberRole.MEMBER);
        groupMemberRepository.save(member);

        group.incrementMemberCount();
        groupRepository.save(group);
    }

    @Transactional
    public void leaveGroup(Long groupId, CustomUser customUser) {
        GroupMember member = groupMemberRepository
            .findByGroupIdAndUsername(groupId, customUser.getUsername())
            .orElseThrow(() -> new AppException(GroupErrorCode.GROUP_NOT_MEMBER));

        if (member.isLeader()) {
            throw new AppException(GroupErrorCode.GROUP_LEADER_CANNOT_LEAVE);
        }

        Group group = findGroup(groupId);
        groupMemberRepository.delete(member);
        group.decrementMemberCount();
        groupRepository.save(group);
    }

    @Transactional
    public void kickMember(Long groupId, String targetUsername, CustomUser customUser) {
        Group group = findGroup(groupId);
        validateLeader(group, customUser.getUsername());

        GroupMember targetMember = groupMemberRepository
            .findByGroupIdAndUsername(groupId, targetUsername)
            .orElseThrow(() -> new AppException(GroupErrorCode.GROUP_MEMBER_NOT_FOUND));

        groupMemberRepository.delete(targetMember);
        group.decrementMemberCount();
        groupRepository.save(group);
    }

    @Transactional(readOnly = true)
    public List<GroupMemberResponse> getMembers(Long groupId) {
        findGroup(groupId);
        return groupMemberRepository.findByGroupId(groupId)
            .stream()
            .map(GroupMemberResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<GroupResponse> getMyGroups(CustomUser customUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Group> groups = groupRepository.findByMemberUsername(customUser.getUsername(), pageable);
        return PageResponse.from(groups.map(GroupResponse::from));
    }

    private Group findGroup(Long groupId) {
        return groupRepository.findById(groupId)
            .orElseThrow(() -> new AppException(GroupErrorCode.GROUP_NOT_FOUND));
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    }

    private void validateLeader(Group group, String username) {
        if (!group.getLeader().getUsername().equals(username)) {
            throw new AppException(GroupErrorCode.GROUP_FORBIDDEN);
        }
    }
}
