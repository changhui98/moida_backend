package com.peopleground.moida.content.infrastructure.repository;

import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.content.domain.entity.QContent;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Content> findAllContents(Pageable pageable) {

        QContent content = QContent.content;

        List<Content> contents = queryFactory
            .selectFrom(content)
            .where(content.deletedDate.isNull())
            .orderBy(content.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(content.count())
            .from(content)
            .where(content.deletedDate.isNull())
            .fetchOne();

        return new PageImpl<>(contents, pageable, total != null ? total : 0);
    }
}
