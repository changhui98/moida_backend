package com.peopleground.moida.image.domain.repository;

import com.peopleground.moida.image.domain.entity.Image;
import com.peopleground.moida.image.domain.entity.ImageTargetType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ImageRepository {

    Image save(Image image);

    Optional<Image> findById(Long id);

    List<Image> findByTarget(ImageTargetType targetType, String targetId);

    Map<String, List<String>> findUrlsByTargetIds(ImageTargetType targetType, List<String> targetIds);

    void deleteById(Long id);
}
