package com.peopleground.moida.global.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 캐시 설정.
 *
 * <p>캐시별 TTL 전략 (Cache Stampede 방지를 위해 각 TTL에 랜덤 지터 적용)</p>
 * <ul>
 *   <li>popularTags: 10분 + 랜덤 지터(0~60초)</li>
 *   <li>tagAutocomplete: 30분 + 랜덤 지터(0~120초)</li>
 *   <li>contentList: 3분 + 랜덤 지터(0~30초)</li>
 *   <li>contentLikeCount: 1시간 + 랜덤 지터(0~300초)</li>
 *   <li>commentLikeCount: 1시간 + 랜덤 지터(0~300초)</li>
 * </ul>
 *
 * <p>Redis 장애 시 Fallback 처리는 각 Service 레이어에서 try-catch로 처리한다.</p>
 *
 * <p>직렬화: Spring Data Redis의 RedisSerializer.json() 팩토리 메서드를 통해 ObjectMapper 기반
 * JSON 직렬화기를 생성한다. JavaTimeModule 및 ActivateDefaultTyping으로 LocalDateTime 포함 다형성 타입 지원.</p>
 */
@Configuration
@EnableCaching
public class RedisConfig {

    private static final Random JITTER_RANDOM = new Random();

    /**
     * LocalDateTime 등 Java 8+ 시간 타입 직렬화를 위해 JavaTimeModule을 등록한 ObjectMapper 기반 직렬화기.
     * 다형성 지원(ActivateDefaultTyping)으로 역직렬화 시 원래 타입 복원을 보장한다.
     * RedisSerializer.json()은 내부적으로 JdkSerializationRedisSerializer의 대안으로 사용된다.
     */
    private ObjectMapper buildRedisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }

    @SuppressWarnings("unchecked")
    private RedisSerializer<Object> buildJsonSerializer() {
        // Spring Data Redis에서 ObjectMapper를 받는 RedisSerializer.json() 오버로드가 없으므로
        // 커스텀 ObjectMapper를 사용하는 MoidaJsonRedisSerializer를 사용한다.
        return new MoidaJsonRedisSerializer(buildRedisObjectMapper());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        RedisSerializer<Object> jsonSerializer = buildJsonSerializer();
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisSerializer<Object> jsonSerializer = buildJsonSerializer();

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
            .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 인기 태그 목록: 10분 + 지터(0~60초)
        cacheConfigurations.put("popularTags",
            defaultConfig.entryTtl(Duration.ofSeconds(600 + JITTER_RANDOM.nextInt(60))));

        // 태그 자동완성: 30분 + 지터(0~120초)
        cacheConfigurations.put("tagAutocomplete",
            defaultConfig.entryTtl(Duration.ofSeconds(1800 + JITTER_RANDOM.nextInt(120))));

        // 게시글 목록 1페이지: 3분 + 지터(0~30초)
        cacheConfigurations.put("contentList",
            defaultConfig.entryTtl(Duration.ofSeconds(180 + JITTER_RANDOM.nextInt(30))));

        // 게시글 좋아요 카운트: 1시간 + 지터(0~300초)
        cacheConfigurations.put("contentLikeCount",
            defaultConfig.entryTtl(Duration.ofSeconds(3600 + JITTER_RANDOM.nextInt(300))));

        // 댓글 좋아요 카운트: 1시간 + 지터(0~300초)
        cacheConfigurations.put("commentLikeCount",
            defaultConfig.entryTtl(Duration.ofSeconds(3600 + JITTER_RANDOM.nextInt(300))));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}
