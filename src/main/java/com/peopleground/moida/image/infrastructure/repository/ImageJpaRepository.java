package com.peopleground.moida.image.infrastructure.repository;

import com.peopleground.moida.image.domain.entity.Image;
import com.peopleground.moida.image.domain.entity.ImageTargetType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageJpaRepository extends JpaRepository<Image, Long> {

    List<Image> findByTargetTypeAndTargetIdOrderBySortOrderAsc(ImageTargetType targetType, String targetId);
}
