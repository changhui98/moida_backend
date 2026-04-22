package com.peopleground.moida.global.redis;

import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 게시글 조회수 Redis Write-through 서비스.
 *
 * <p>조회수는 Redis에 먼저 기록하고 5분 주기 배치(@Scheduled)로 DB에 반영한다.
 * Key 형식: "viewCount:{contentId}" (영구 보존)</p>
 *
 * <p>Redis 장애 시에는 RedisFallbackUtil을 통해 조용히 실패 처리한다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountService {

    private static final String VIEW_COUNT_KEY_PREFIX = "viewCount:";
    private static final String DIRTY_VIEW_COUNT_SET = "dirtyViewCounts";

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisFallbackUtil redisFallbackUtil;

    /**
     * 게시글 조회수를 1 증가시킨다. Redis에 기록하고, 변경된 키를 dirty set에 추가한다.
     */
    public void incrementViewCount(Long contentId) {
        redisFallbackUtil.executeWriteWithFallback(() -> {
            String key = VIEW_COUNT_KEY_PREFIX + contentId;
            redisTemplate.opsForValue().increment(key);
            redisTemplate.opsForSet().add(DIRTY_VIEW_COUNT_SET, String.valueOf(contentId));
        });
    }

    /**
     * 특정 게시글의 현재 조회수를 조회한다.
     */
    public Long getViewCount(Long contentId) {
        return redisFallbackUtil.executeWithFallback(() -> {
            String key = VIEW_COUNT_KEY_PREFIX + contentId;
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? Long.parseLong(value.toString()) : 0L;
        }, () -> 0L);
    }

    /**
     * dirty set에서 변경된 contentId 목록을 가져온 뒤 set을 비운다.
     * ViewCountBatchSynchronizer에서 호출한다.
     */
    public Set<Object> getDirtyContentIds() {
        return redisFallbackUtil.executeWithFallback(() -> {
            Set<Object> members = redisTemplate.opsForSet().members(DIRTY_VIEW_COUNT_SET);
            redisTemplate.delete(DIRTY_VIEW_COUNT_SET);
            return members;
        }, Set::of);
    }

    /**
     * Redis에 저장된 특정 게시글의 조회수를 초기값으로 설정한다. (DB 동기화 완료 후 호출)
     */
    public void initViewCount(Long contentId, long count) {
        redisFallbackUtil.executeWriteWithFallback(() -> {
            String key = VIEW_COUNT_KEY_PREFIX + contentId;
            redisTemplate.opsForValue().set(key, String.valueOf(count));
        });
    }
}
