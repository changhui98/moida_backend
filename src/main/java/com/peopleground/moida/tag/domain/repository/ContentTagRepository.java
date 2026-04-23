package com.peopleground.moida.tag.domain.repository;

import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.tag.domain.entity.ContentTag;
import java.util.Collection;
import java.util.List;

public interface ContentTagRepository {

    ContentTag save(ContentTag contentTag);

    List<ContentTag> saveAll(Iterable<ContentTag> contentTags);

    List<ContentTag> findAllByContent(Content content);

    List<ContentTag> findAllFetchTagByContentIdIn(Collection<Long> contentIds);

    void deleteAllByContent(Content content);
}
