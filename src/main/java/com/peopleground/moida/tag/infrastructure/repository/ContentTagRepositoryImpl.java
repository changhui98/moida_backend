package com.peopleground.moida.tag.infrastructure.repository;

import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.tag.domain.entity.ContentTag;
import com.peopleground.moida.tag.domain.repository.ContentTagRepository;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentTagRepositoryImpl implements ContentTagRepository {

    private final ContentTagJpaRepository contentTagJpaRepository;
    private final ContentTagQueryRepository contentTagQueryRepository;

    @Override
    public ContentTag save(ContentTag contentTag) {
        return contentTagJpaRepository.save(contentTag);
    }

    @Override
    public List<ContentTag> saveAll(Iterable<ContentTag> contentTags) {
        return contentTagJpaRepository.saveAll(contentTags);
    }

    @Override
    public List<ContentTag> findAllByContent(Content content) {
        return contentTagJpaRepository.findAllByContent(content);
    }

    @Override
    public List<ContentTag> findAllFetchTagByContentIdIn(Collection<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return List.of();
        }
        return contentTagQueryRepository.findAllFetchTagByContentIdIn(contentIds);
    }

    @Override
    public void deleteAllByContent(Content content) {
        contentTagJpaRepository.deleteAllByContent(content);
    }
}
