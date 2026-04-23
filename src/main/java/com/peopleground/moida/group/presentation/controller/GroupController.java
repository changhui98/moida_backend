package com.peopleground.moida.group.presentation.controller;

import com.peopleground.moida.global.configure.CustomUser;
import com.peopleground.moida.global.dto.PageResponse;
import com.peopleground.moida.group.application.service.GroupService;
import com.peopleground.moida.group.presentation.dto.request.GroupCreateRequest;
import com.peopleground.moida.group.presentation.dto.request.GroupUpdateRequest;
import com.peopleground.moida.group.presentation.dto.response.GroupDetailResponse;
import com.peopleground.moida.group.presentation.dto.response.GroupMemberResponse;
import com.peopleground.moida.group.presentation.dto.response.GroupResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
        @Valid @RequestBody GroupCreateRequest request,
        @AuthenticationPrincipal CustomUser customUser
    ) {
        GroupResponse response = groupService.createGroup(request, customUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<GroupResponse>> getGroups(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<GroupResponse> response = groupService.getGroups(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailResponse> getGroup(@PathVariable Long groupId) {
        GroupDetailResponse response = groupService.getGroup(groupId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{groupId}")
    public ResponseEntity<GroupResponse> updateGroup(
        @PathVariable Long groupId,
        @Valid @RequestBody GroupUpdateRequest request,
        @AuthenticationPrincipal CustomUser customUser
    ) {
        GroupResponse response = groupService.updateGroup(groupId, request, customUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
        @PathVariable Long groupId,
        @AuthenticationPrincipal CustomUser customUser
    ) {
        groupService.deleteGroup(groupId, customUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<Void> joinGroup(
        @PathVariable Long groupId,
        @AuthenticationPrincipal CustomUser customUser
    ) {
        groupService.joinGroup(groupId, customUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(
        @PathVariable Long groupId,
        @AuthenticationPrincipal CustomUser customUser
    ) {
        groupService.leaveGroup(groupId, customUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/members/{username}")
    public ResponseEntity<Void> kickMember(
        @PathVariable Long groupId,
        @PathVariable String username,
        @AuthenticationPrincipal CustomUser customUser
    ) {
        groupService.kickMember(groupId, username, customUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponse>> getMembers(@PathVariable Long groupId) {
        List<GroupMemberResponse> response = groupService.getMembers(groupId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<PageResponse<GroupResponse>> getMyGroups(
        @AuthenticationPrincipal CustomUser customUser,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<GroupResponse> response = groupService.getMyGroups(customUser, page, size);
        return ResponseEntity.ok(response);
    }
}
