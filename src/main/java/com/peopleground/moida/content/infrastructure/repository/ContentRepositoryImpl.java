package com.peopleground.moida.content.infrastructure.repository;

import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.content.domain.repository.ContentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepository {

    private final ContentJpaRepository contentJpaRepository;
    private final ContentQueryRepository contentQueryRepository;

    @Override
    public Content save(Content content) {

        return contentJpaRepository.save(content);
    }

    @Override
    public Optional<Content> findById(Long id) {

        return contentQueryRepository.findById(id);
    }

    @Override
    public Page<Content> findAllContents(Pageable pageable) {

        return contentQueryRepository.findAllContents(pageable);
    }

    @Override
    public Page<Content> findAllContentsIncludingDeleted(Pageable pageable) {

        return contentQueryRepository.findAllContentsIncludingDeleted(pageable);
    }
}
