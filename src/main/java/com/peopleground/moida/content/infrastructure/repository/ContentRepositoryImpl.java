package com.peopleground.moida.content.infrastructure.repository;

import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.content.domain.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepository {

    private final ContentJpaRepository contentJpaRepository;

    @Override
    public Content save(Content content) {

        return contentJpaRepository.save(content);
    }
}
