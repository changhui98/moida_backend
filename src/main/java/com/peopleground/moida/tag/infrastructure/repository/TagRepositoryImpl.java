package com.peopleground.moida.tag.infrastructure.repository;

import com.peopleground.moida.tag.domain.entity.Tag;
import com.peopleground.moida.tag.domain.repository.TagRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepository {

    private final TagJpaRepository tagJpaRepository;
    private final TagQueryRepository tagQueryRepository;

    @Override
    public Tag save(Tag tag) {
        return tagJpaRepository.save(tag);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return tagJpaRepository.findByName(name);
    }

    @Override
    public List<Tag> findTopByPostCount(int limit) {
        return tagQueryRepository.findTopByPostCount(limit);
    }

    @Override
    public List<Tag> searchByNameContaining(String keyword, int limit) {
        return tagQueryRepository.searchByNameContaining(keyword, limit);
    }
}
