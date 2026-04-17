package com.peopleground.moida.content.application.service;

import com.peopleground.moida.content.domain.repository.ContentRepository;
import com.peopleground.moida.content.presentation.dto.response.AdminContentResponse;
import com.peopleground.moida.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public PageResponse<AdminContentResponse> getAllContents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return PageResponse.from(
            contentRepository.findAllContentsIncludingDeleted(pageable)
                .map(AdminContentResponse::from)
        );
    }
}
