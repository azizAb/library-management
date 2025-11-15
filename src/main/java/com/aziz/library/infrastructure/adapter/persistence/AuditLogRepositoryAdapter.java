package com.aziz.library.infrastructure.adapter.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.aziz.library.domain.model.AuditLog;
import com.aziz.library.domain.port.out.AuditLogRepositoryPort;
import com.aziz.library.infrastructure.adapter.persistence.entity.AuditLogEntity;
import com.aziz.library.infrastructure.adapter.persistence.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepositoryPort{

    private final AuditLogRepository auditLogRepository;
    
    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogEntity entity = toEntity(auditLog);
        AuditLogEntity saved = auditLogRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public List<AuditLog> findAll() {
        return auditLogRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLog> findByUserId(Long userId) {
        return auditLogRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLog> findByAction(String action) {
        return auditLogRepository.findByAction(action).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    private AuditLog toDomain(AuditLogEntity entity) {
        return AuditLog.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .action(entity.getAction())
                .entity(entity.getEntity())
                .entityId(entity.getEntityId())
                .details(entity.getDetails())
                .ipAddress(entity.getIpAddress())
                .browser(entity.getBrowser())
                .device(entity.getDevice())
                .operatingSystem(entity.getOperatingSystem())
                .timestamp(entity.getTimestamp())
                .build();
    }
    
    private AuditLogEntity toEntity(AuditLog domain) {
        return AuditLogEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .username(domain.getUsername())
                .action(domain.getAction())
                .entity(domain.getEntity())
                .entityId(domain.getEntityId())
                .details(domain.getDetails())
                .ipAddress(domain.getIpAddress())
                .browser(domain.getBrowser())
                .device(domain.getDevice())
                .operatingSystem(domain.getOperatingSystem())
                .timestamp(domain.getTimestamp())
                .build();
    }

}
