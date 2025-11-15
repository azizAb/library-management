package com.aziz.library.infrastructure.adapter.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aziz.library.infrastructure.adapter.persistence.entity.AuditLogEntity;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    List<AuditLogEntity> findByUserId(Long userId);
    List<AuditLogEntity> findByAction(String action);
    List<AuditLogEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
