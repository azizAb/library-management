package com.aziz.library.infrastructure.adapter.cache;

import com.aziz.library.domain.port.out.CacheServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisCacheServiceAdapterTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    
    @Mock
    private ValueOperations<String, Object> valueOperations;
    
    @InjectMocks
    private RedisCacheServiceAdapter cacheService;
    
    @Test
    void testSet_ShouldCacheValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        
        cacheService.set("test-key", "test-value", 3600L);
        
        verify(valueOperations).set("test-key", "test-value", 3600L, TimeUnit.SECONDS);
    }
    
    @Test
    void testGet_WithExistingKey_ShouldReturnValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("test-key")).thenReturn("test-value");
        
        Optional<Object> result = cacheService.get("test-key");
        
        assertTrue(result.isPresent());
        assertEquals("test-value", result.get());
    }
    
    @Test
    void testGet_WithNonExistingKey_ShouldReturnEmpty() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("test-key")).thenReturn(null);
        
        Optional<Object> result = cacheService.get("test-key");
        
        assertFalse(result.isPresent());
    }
    
    @Test
    void testDelete_ShouldDeleteKey() {
        cacheService.delete("test-key");
        
        verify(redisTemplate).delete("test-key");
    }
    
    @Test
    void testExists_WithExistingKey_ShouldReturnTrue() {
        when(redisTemplate.hasKey("test-key")).thenReturn(true);
        
        boolean exists = cacheService.exists("test-key");
        
        assertTrue(exists);
    }
    
    @Test
    void testExists_WithNonExistingKey_ShouldReturnFalse() {
        when(redisTemplate.hasKey("test-key")).thenReturn(false);
        
        boolean exists = cacheService.exists("test-key");
        
        assertFalse(exists);
    }

    @Test
    void testSet_WhenExceptionThrown_ShouldLogError() {
        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis error"));
        // Should not throw, just log
        assertDoesNotThrow(() -> cacheService.set("err-key", "val", 10L));
    }

    @Test
    void testGet_WhenExceptionThrown_ShouldReturnEmptyAndLog() {
        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis error"));
        Optional<Object> result = cacheService.get("err-key");
        assertFalse(result.isPresent());
    }

    @Test
    void testDelete_WhenExceptionThrown_ShouldLogError() {
        doThrow(new RuntimeException("Redis error")).when(redisTemplate).delete("err-key");
        // Should not throw, just log
        assertDoesNotThrow(() -> cacheService.delete("err-key"));
    }

    @Test
    void testExists_WhenExceptionThrown_ShouldReturnFalseAndLog() {
        when(redisTemplate.hasKey("err-key")).thenThrow(new RuntimeException("Redis error"));
        boolean exists = cacheService.exists("err-key");
        assertFalse(exists);
    }
}
