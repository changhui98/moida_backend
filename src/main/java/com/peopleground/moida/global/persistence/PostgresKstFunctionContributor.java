package com.peopleground.moida.global.persistence;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

/**
 * Hibernate 7 의 HQL 파서는 `to_char(expr AT TIME ZONE 'X', 'pattern')` 처럼
 * 함수 인자 내부에 `AT TIME ZONE` 을 섞어 쓰는 문법을 허용하지 않는다.
 * (AT TIME ZONE 은 HQL 에서 top-level 표현식으로만 허용됨.)
 *
 * 따라서 타임존 변환과 포맷팅을 묶은 PostgreSQL 전용 SQL 조각을
 * 커스텀 HQL 함수로 등록하여 HQL 레벨에서는 단일 함수 호출만 보이도록 한다.
 * <p>
 * 등록 함수:
 * <ul>
 *     <li>{@code to_char_kst_month(timestamp)}
 *     → UTC 로 저장된 timestamp 를 Asia/Seoul 로 변환 후 {@code 'YYYY-MM'} 문자열로 포맷</li>
 * </ul>
 * <p>
 * 향후 일/주 등 다른 버킷이 필요해지면 동일 패턴으로 {@code to_char_kst_day} 등을 추가한다.
 * <p>
 * 등록 경로: {@code META-INF/services/org.hibernate.boot.model.FunctionContributor}
 */
public class PostgresKstFunctionContributor implements FunctionContributor {

    private static final String KST_MONTH_SQL =
        "to_char(?1 AT TIME ZONE 'UTC' AT TIME ZONE 'Asia/Seoul', 'YYYY-MM')";

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {

        functionContributions.getFunctionRegistry()
            .registerPattern(
                "to_char_kst_month",
                KST_MONTH_SQL,
                functionContributions.getTypeConfiguration()
                    .getBasicTypeRegistry()
                    .resolve(StandardBasicTypes.STRING)
            );
    }
}
