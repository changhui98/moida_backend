package com.peopleground.moida.content.domain.repository;

import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.content.presentation.dto.request.SearchType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentRepository {

    Content save(Content content);

    Optional<Content> findById(Long id);

    Optional<Content> findByIdIncludingDeleted(Long id);

    Page<Content> findAllContents(Pageable pageable);

    Page<Content> findAllContentsIncludingDeleted(Pageable pageable);

    Page<Content> searchContents(String keyword, SearchType searchType, Pageable pageable);

    Page<Content> searchContentsIncludingDeleted(String keyword, SearchType searchType, Pageable pageable);
}
