package com.peopleground.moida.tag.infrastructure.repository;

import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.tag.domain.entity.ContentTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentTagJpaRepository extends JpaRepository<ContentTag, Long> {

    List<ContentTag> findAllByContent(Content content);

    void deleteAllByContent(Content content);
}
