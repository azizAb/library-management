package com.aziz.library.domain.port.out;

import java.time.LocalDateTime;
import java.util.List;

import com.aziz.library.domain.model.AuditLog;

public interface AuditLogRepositoryPort {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findAll();
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
