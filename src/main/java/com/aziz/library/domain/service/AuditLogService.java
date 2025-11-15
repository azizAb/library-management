package com.aziz.library.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aziz.library.domain.exception.UnauthorizedException;
import com.aziz.library.domain.model.AuditLog;
import com.aziz.library.domain.model.Role;
import com.aziz.library.domain.model.User;
import com.aziz.library.domain.port.in.AuditLogUseCase;
import com.aziz.library.domain.port.out.AuditLogRepositoryPort;
import com.aziz.library.domain.port.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService implements AuditLogUseCase{

    private final AuditLogRepositoryPort auditLogRepository;
    private final UserRepositoryPort userRepository;
    
    @Override
    public void logAction(Long userId, String action, String entity, Long entityId, 
                         String details, String ipAddress, String userAgent) {
        
        String username = null;
        if (userId != null) {
            username = userRepository.findById(userId)
                    .map(User::getUsername)
                    .orElse(null);
        }
        
        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .details(details)
                .ipAddress(ipAddress)
                .browser(extractBrowser(userAgent))
                .device(extractDevice(userAgent))
                .operatingSystem(extractOS(userAgent))
                .timestamp(LocalDateTime.now())
                .build();
        
        auditLogRepository.save(auditLog);
        log.info("Audit log created: {} by user {}", action, userId);
    }
    
    @Override
    public List<AuditLog> getAllLogs(Long currentUserId) {
        checkSuperAdminAccess(currentUserId);
        log.debug("Retrieving all audit logs");
        return auditLogRepository.findAll();
    }
    
    @Override
    public List<AuditLog> getLogsByUser(Long userId, Long currentUserId) {
        checkSuperAdminAccess(currentUserId);
        log.debug("Retrieving audit logs for user: {}", userId);
        return auditLogRepository.findByUserId(userId);
    }
    
    @Override
    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end, Long currentUserId) {
        checkSuperAdminAccess(currentUserId);
        log.debug("Retrieving audit logs from {} to {}", start, end);
        return auditLogRepository.findByTimestampBetween(start, end);
    }
    
    private void checkSuperAdminAccess(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        if (user.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only SUPER_ADMIN can access audit logs");
        }
    }
    
    private String extractBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("Edge")) return "Edge";
        return "Other";
    }
    
    private String extractDevice(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Mobile")) return "Mobile";
        if (userAgent.contains("Tablet")) return "Tablet";
        return "Desktop";
    }
    
    private String extractOS(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "macOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iOS")) return "iOS";
        return "Other";
    }

}
