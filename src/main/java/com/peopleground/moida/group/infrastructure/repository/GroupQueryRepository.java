package com.peopleground.moida.group.infrastructure.repository;

import com.peopleground.moida.group.domain.entity.Group;
import com.peopleground.moida.group.domain.entity.GroupCategory;
import com.peopleground.moida.group.domain.entity.QGroup;
import com.peopleground.moida.group.domain.entity.QGroupMember;
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

// 빌드 후 Q클래스 자동 생성됨 (QGroup.group, QGroupMember.groupMember, QUser.user)
@Repository
@RequiredArgsConstructor
public class GroupQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Group> findById(Long id) {
        QGroup group = QGroup.group;

        return Optional.ofNullable(
            queryFactory
                .selectFrom(group)
                .where(
                    group.id.eq(id),
                    group.deletedDate.isNull()
                )
                .fetchOne()
        );
    }

    public Page<Group> findAll(Pageable pageable) {
        QGroup group = QGroup.group;

        List<Group> groups = queryFactory
            .selectFrom(group)
            .where(group.deletedDate.isNull())
            .orderBy(group.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(group.count())
            .from(group)
            .where(group.deletedDate.isNull())
            .fetchOne();

        return new PageImpl<>(groups, pageable, total != null ? total : 0);
    }

    public Page<Group> findAll(Pageable pageable, String keyword, GroupCategory category) {
        QGroup group = QGroup.group;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(group.deletedDate.isNull());

        if (keyword != null && !keyword.isBlank()) {
            builder.and(group.name.containsIgnoreCase(keyword));
        }
        if (category != null) {
            builder.and(group.category.eq(category));
        }

        List<Group> groups = queryFactory
            .selectFrom(group)
            .where(builder)
            .orderBy(group.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(group.count())
            .from(group)
            .where(builder)
            .fetchOne();

        return new PageImpl<>(groups, pageable, total != null ? total : 0);
    }

    public Page<Group> findByMemberUsername(String username, Pageable pageable) {
        QGroup group = QGroup.group;
        QGroupMember groupMember = QGroupMember.groupMember;
        QUser user = QUser.user;

        List<Group> groups = queryFactory
            .selectFrom(group)
            .join(groupMember).on(groupMember.group.eq(group))
            .join(groupMember.user, user)
            .where(
                user.username.eq(username),
                group.deletedDate.isNull(),
                groupMember.deletedDate.isNull()
            )
            .orderBy(group.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(group.count())
            .from(group)
            .join(groupMember).on(groupMember.group.eq(group))
            .join(groupMember.user, user)
            .where(
                user.username.eq(username),
                group.deletedDate.isNull(),
                groupMember.deletedDate.isNull()
            )
            .fetchOne();

        return new PageImpl<>(groups, pageable, total != null ? total : 0);
    }
}
