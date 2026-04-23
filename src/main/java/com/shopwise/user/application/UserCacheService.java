package com.shopwise.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService {

    private final StringRedisTemplate redisTemplate;
    private static final String WHITELIST_KEY = "whitelist:";

    public void addToWhitelist(Long userId) {
        redisTemplate.opsForValue()
                .set(WHITELIST_KEY + userId, "1", 1, TimeUnit.DAYS);
        log.debug("User added to whitelist: {}", userId);
    }

    public boolean isWhitelisted(Long userId) {
        Boolean exists = redisTemplate.hasKey(WHITELIST_KEY + userId);
        return Boolean.TRUE.equals(exists);
    }

    public void removeFromWhitelist(Long userId) {
        redisTemplate.delete(WHITELIST_KEY + userId);
        log.info("User removed from whitelist: {}", userId);
    }
}