package com.peopleground.moida.user.infrastructure.repository;

import com.peopleground.moida.user.domain.entity.QUser;
import com.peopleground.moida.user.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<User> findAllUsers(Pageable pageable) {

        QUser user = QUser.user;

        List<User> content = queryFactory
            .selectFrom(user)
            .where(user.deletedDate.isNull())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(user.count())
            .from(user)
            .where(user.deletedDate.isNull())
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    public Page<User> findAllUsersForAdmin(Pageable pageable) {

        QUser user = QUser.user;

        List<User> content = queryFactory
            .selectFrom(user)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(user.count())
            .from(user)
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
