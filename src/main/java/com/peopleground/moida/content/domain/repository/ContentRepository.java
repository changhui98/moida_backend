package com.peopleground.moida.content.domain.repository;

import com.peopleground.moida.content.domain.entity.Content;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentRepository {

    Content save(Content content);

    Optional<Content> findById(Long id);

    Page<Content> findAllContents(Pageable pageable);
}
