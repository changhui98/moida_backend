package com.peopleground.moida.content.infrastructure.repository;

import com.peopleground.moida.content.domain.entity.Content;
import com.peopleground.moida.content.domain.entity.QContent;
import com.peopleground.moida.content.presentation.dto.request.SearchType;
import com.peopleground.moida.user.domain.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Content> findById(Long id) {

        QContent content = QContent.content;

        return Optional.ofNullable(
            queryFactory
                .selectFrom(content)
                .where(
                    content.id.eq(id),
                    content.deletedDate.isNull()
                )
                .fetchOne()
        );
    }

    public Optional<Content> findByIdIncludingDeleted(Long id) {

        QContent content = QContent.content;

        return Optional.ofNullable(
            queryFactory
                .selectFrom(content)
                .where(content.id.eq(id))
                .fetchOne()
        );
    }

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

    public Page<Content> findAllContentsIncludingDeleted(Pageable pageable) {

        QContent content = QContent.content;

        List<Content> contents = queryFactory
            .selectFrom(content)
            .orderBy(content.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(content.count())
            .from(content)
            .fetchOne();

        return new PageImpl<>(contents, pageable, total != null ? total : 0);
    }

    public Page<Content> searchContents(String keyword, SearchType searchType, Pageable pageable) {

        QContent content = QContent.content;
        QUser user = QUser.user;
        BooleanBuilder condition = buildSearchCondition(content, user, keyword, searchType);
        condition.and(content.deletedDate.isNull());

        List<Content> contents = queryFactory
            .selectFrom(content)
            .join(content.user, user)
            .where(condition)
            .orderBy(content.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(content.count())
            .from(content)
            .join(content.user, user)
            .where(condition)
            .fetchOne();

        return new PageImpl<>(contents, pageable, total != null ? total : 0);
    }

    public Page<Content> searchContentsIncludingDeleted(String keyword, SearchType searchType, Pageable pageable) {

        QContent content = QContent.content;
        QUser user = QUser.user;
        BooleanBuilder condition = buildSearchCondition(content, user, keyword, searchType);

        List<Content> contents = queryFactory
            .selectFrom(content)
            .join(content.user, user)
            .where(condition)
            .orderBy(content.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(content.count())
            .from(content)
            .join(content.user, user)
            .where(condition)
            .fetchOne();

        return new PageImpl<>(contents, pageable, total != null ? total : 0);
    }

    private BooleanBuilder buildSearchCondition(QContent content, QUser user, String keyword, SearchType searchType) {

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword == null || keyword.isBlank()) {
            return builder;
        }

        if (searchType == SearchType.USERNAME) {
            builder.and(user.username.containsIgnoreCase(keyword));
        } else {
            builder.and(content.title.containsIgnoreCase(keyword));
        }

        return builder;
    }
}
