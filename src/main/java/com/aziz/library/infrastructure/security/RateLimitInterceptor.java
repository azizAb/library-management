package com.aziz.library.infrastructure.security;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor{

    private final Map<String, Bucket> rateLimiterCache;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String key = getClientKey(request);
        Bucket bucket = resolveBucket(key);
        
        if (bucket.tryConsume(1)) {
            return true;
        }
        
        log.warn("Rate limit exceeded for: {}", key);
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.getWriter().write("{\"error\": \"Rate limit exceeded. Please try again later.\"}");
        return false;
    }
    
    private Bucket resolveBucket(String key) {
        return rateLimiterCache.computeIfAbsent(key, k -> {
            io.github.bucket4j.Bandwidth limit = io.github.bucket4j.Bandwidth.classic(
                60, 
                io.github.bucket4j.Refill.intervally(60, java.time.Duration.ofMinutes(1))
            );
            return io.github.bucket4j.Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }
    
    private String getClientKey(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "user:" + auth.getName();
        }
        
        // For anonymous users, use IP address
        String clientIp = getClientIpAddress(request);
        return "ip:" + clientIp;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}
