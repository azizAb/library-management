package com.aziz.library.domain.port.in;

import java.time.LocalDateTime;
import java.util.List;

import com.aziz.library.domain.model.AuditLog;

public interface AuditLogUseCase {
    void logAction(Long userId, String action, String entity, Long entityId, 
                   String details, String ipAddress, String userAgent);
    List<AuditLog> getAllLogs(Long currentUserId);
    List<AuditLog> getLogsByUser(Long userId, Long currentUserId);
    List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end, Long currentUserId);
}
