package com.peopleground.moida.image.infrastructure.repository;

import com.peopleground.moida.image.domain.entity.Image;
import com.peopleground.moida.image.domain.entity.ImageTargetType;
import com.peopleground.moida.image.domain.repository.ImageRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryImpl implements ImageRepository {

    private final ImageJpaRepository imageJpaRepository;

    @Override
    public Image save(Image image) {
        return imageJpaRepository.save(image);
    }

    @Override
    public Optional<Image> findById(Long id) {
        return imageJpaRepository.findById(id);
    }

    @Override
    public List<Image> findByTarget(ImageTargetType targetType, String targetId) {
        return imageJpaRepository.findByTargetTypeAndTargetIdOrderBySortOrderAsc(targetType, targetId);
    }

    @Override
    public void deleteById(Long id) {
        imageJpaRepository.deleteById(id);
    }
}
