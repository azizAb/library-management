package com.aziz.library.domain.port.out;

import java.util.Optional;

public interface CacheServicePort {
    void set(String key, Object value, long ttlSeconds);
    Optional<Object> get(String key);
    void delete(String key);
    boolean exists(String key);
}
