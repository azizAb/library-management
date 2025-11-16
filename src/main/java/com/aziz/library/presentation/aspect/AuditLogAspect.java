package com.aziz.library.presentation.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.aziz.library.domain.port.in.AuditLogUseCase;
import com.aziz.library.infrastructure.security.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogUseCase auditLogUseCase;
    private final CustomUserDetailsService userDetailsService;
    
    @AfterReturning(
        pointcut = "execution(* com.aziz.library.presentation.controller.ArticleController.createArticle(..))",
        returning = "result"
    )
    public void logCreateArticle(JoinPoint joinPoint, Object result) {
        logAction("CREATE_ARTICLE", "ARTICLE", "Article created");
    }

    @AfterReturning(
        pointcut = "execution(* com.aziz.library.presentation.controller.ArticleController.getAllArticles(..))",
        returning = "result"
    )
    public void logGetAllArticle(JoinPoint joinPoint, Object result) {
        logAction("GET_ALL_ARTICLE", "ARTICLE", "Article Retrieve");
    }

    @AfterReturning(
        pointcut = "execution(* com.aziz.library.presentation.controller.ArticleController.getArticleById(..))",
        returning = "result"
    )
    public void logGetArticle(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long articleId = (Long) args[0];
        logAction("GET_ARTICLE", "ARTICLE", "Article Retrieve: " + articleId);
    }
    
    @AfterReturning(
        pointcut = "execution(* com.aziz.library.presentation.controller.ArticleController.updateArticle(..))",
        returning = "result"
    )
    public void logUpdateArticle(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long articleId = (Long) args[0];
        logAction("UPDATE_ARTICLE", "ARTICLE", "Article updated: " + articleId);
    }
    
    @AfterReturning(
        pointcut = "execution(* com.aziz.library.presentation.controller.ArticleController.deleteArticle(..))"
    )
    public void logDeleteArticle(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long articleId = (Long) args[0];
        logAction("DELETE_ARTICLE", "ARTICLE", "Article deleted: " + articleId);
    }
    
    @AfterReturning(
        pointcut = "execution(* com.aziz.library.presentation.controller.UserController.createUser(..))",
        returning = "result"
    )
    public void logCreateUser(JoinPoint joinPoint, Object result) {
        logAction("CREATE_USER", "USER", "User created");
    }
    
    @AfterReturning(
        pointcut = "execution(* com.aziz.library.presentation.controller.UserController.updateUser(..))",
        returning = "result"
    )
    public void logUpdateUser(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        logAction("UPDATE_USER", "USER", "User updated: " + userId);
    }
    
    @AfterReturning(
        pointcut = "execution(* com.aziz.library.presentation.controller.UserController.deleteUser(..))"
    )
    public void logDeleteUser(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        logAction("DELETE_USER", "USER", "User deleted: " + userId);
    }
    
    @AfterReturning(
        pointcut = "execution(* com.aziz.library.presentation.controller.AuthController.login(..))"
    )
    public void logLogin(JoinPoint joinPoint) {
        logAction("LOGIN", "AUTH", "User login attempt");
    }
    
    @AfterReturning(
        pointcut = "execution(* com.aziz.library.domain.service.AuthenticationService.register(..))"
    )
    public void logRegister(JoinPoint joinPoint) {
        logAction("REGISTER", "AUTH", "User registration");
    }
    
    private void logAction(String action, String entity, String details) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();
            if (request == null) {
                return;
            }
            
            Long userId = getCurrentUserId();
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            auditLogUseCase.logAction(userId, action, entity, null, details, ipAddress, userAgent);
        } catch (Exception e) {
            log.error("Error logging audit action: {}", action, e);
        }
    }
    
    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                return userDetailsService.getUserIdByUsername(auth.getName());
            }
        } catch (Exception e) {
            log.debug("Could not get current user ID", e);
        }
        return null;
    }
    
    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        String remoteAddr = request.getRemoteAddr();
        
        // Convert IPv6 localhost to IPv4
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || "::1".equals(remoteAddr)) {
            return "127.0.0.1";
        }
        
        return remoteAddr;
    }

}
