package com.peopleground.moida.global.application;

import com.peopleground.moida.content.domain.repository.ContentRepository;
import com.peopleground.moida.global.dto.MonthlyStatsPoint;
import com.peopleground.moida.global.dto.MonthlyStatsResponse;
import com.peopleground.moida.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private static final String TIMEZONE = "Asia/Seoul";
    private static final ZoneId KST = ZoneId.of(TIMEZONE);

    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public MonthlyStatsResponse getMonthlySignups(int months) {

        YearMonth now = YearMonth.now(KST);
        YearMonth startMonth = now.minusMonths(months - 1);
        LocalDateTime windowStart = toUtcLocalDateTime(startMonth);

        Map<String, Long> dbResult = userRepository.countMonthlySignups(windowStart);

        List<MonthlyStatsPoint> points = backfill(startMonth, months, dbResult);
        return new MonthlyStatsResponse(TIMEZONE, points);
    }

    @Transactional(readOnly = true)
    public MonthlyStatsResponse getMonthlyContentCreations(int months) {

        YearMonth now = YearMonth.now(KST);
        YearMonth startMonth = now.minusMonths(months - 1);
        LocalDateTime windowStart = toUtcLocalDateTime(startMonth);

        Map<String, Long> dbResult = contentRepository.countMonthlyCreations(windowStart);

        List<MonthlyStatsPoint> points = backfill(startMonth, months, dbResult);
        return new MonthlyStatsResponse(TIMEZONE, points);
    }

    private LocalDateTime toUtcLocalDateTime(YearMonth month) {
        ZonedDateTime kstStart = month.atDay(1).atStartOfDay(KST);
        return kstStart.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    }

    private List<MonthlyStatsPoint> backfill(YearMonth startMonth, int months, Map<String, Long> dbResult) {

        List<MonthlyStatsPoint> points = new ArrayList<>(months);

        for (int i = 0; i < months; i++) {
            YearMonth ym = startMonth.plusMonths(i);
            String key = ym.toString();
            long count = dbResult.getOrDefault(key, 0L);
            points.add(new MonthlyStatsPoint(key, count));
        }

        return points;
    }
}
