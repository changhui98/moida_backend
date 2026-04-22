package com.peopleground.moida.tag.presentation.controller;

import com.peopleground.moida.content.presentation.dto.response.ContentResponse;
import com.peopleground.moida.global.dto.PageResponse;
import com.peopleground.moida.tag.application.service.TagService;
import com.peopleground.moida.tag.presentation.dto.response.TagResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    /**
     * 인기 태그 목록 조회 (상위 N개, postCount 내림차순)
     */
    @GetMapping
    public ResponseEntity<List<TagResponse>> getPopularTags() {
        List<TagResponse> res = tagService.getPopularTags();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    /**
     * 태그 자동완성 검색 (키워드 부분 일치)
     */
    @GetMapping("/search")
    public ResponseEntity<List<TagResponse>> searchTags(
        @RequestParam String q
    ) {
        List<TagResponse> res = tagService.searchTags(q);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    /**
     * 특정 태그의 게시글 목록 조회
     */
    @GetMapping("/{name}/contents")
    public ResponseEntity<PageResponse<ContentResponse>> getContentsByTagName(
        @PathVariable String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ContentResponse> res = tagService.getContentsByTagName(name, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
