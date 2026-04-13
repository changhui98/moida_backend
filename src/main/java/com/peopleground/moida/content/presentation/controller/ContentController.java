package com.peopleground.moida.content.presentation.controller;

import com.peopleground.moida.content.application.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contents")
public class ContentController {

    private final ContentService contentService;

}
