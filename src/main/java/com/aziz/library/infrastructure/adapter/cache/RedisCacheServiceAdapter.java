package com.aziz.library.infrastructure.adapter.cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.aziz.library.domain.port.out.CacheServicePort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheServiceAdapter implements CacheServicePort{

    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public void set(String key, Object value, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
            log.debug("Cached value for key: {}", key);
        } catch (Exception e) {
            log.error("Error caching value for key: {}", key, e);
        }
    }
    
    @Override
    public Optional<Object> get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(value);
        } catch (Exception e) {
            log.error("Error retrieving cached value for key: {}", key, e);
            return Optional.empty();
        }
    }
    
    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Deleted cache for key: {}", key);
        } catch (Exception e) {
            log.error("Error deleting cache for key: {}", key, e);
        }
    }
    
    @Override
    public boolean exists(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Error checking cache existence for key: {}", key, e);
            return false;
        }
    }

}
