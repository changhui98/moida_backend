package com.peopleground.moida.tag.infrastructure.repository;

import com.peopleground.moida.tag.domain.entity.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
}
