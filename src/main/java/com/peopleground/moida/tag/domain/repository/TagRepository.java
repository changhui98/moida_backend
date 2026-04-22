package com.peopleground.moida.tag.domain.repository;

import com.peopleground.moida.tag.domain.entity.Tag;
import java.util.List;
import java.util.Optional;

public interface TagRepository {

    Tag save(Tag tag);

    Optional<Tag> findByName(String name);

    List<Tag> findTopByPostCount(int limit);

    List<Tag> searchByNameContaining(String keyword, int limit);
}
