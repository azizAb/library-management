package com.aziz.library.infrastructure.adapter.persistence;

import com.aziz.library.domain.model.AuditLog;
import com.aziz.library.infrastructure.adapter.persistence.entity.AuditLogEntity;
import com.aziz.library.infrastructure.adapter.persistence.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditLogRepositoryAdapterTest {

    private AuditLogRepository auditLogRepository;
    private AuditLogRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        auditLogRepository = mock(AuditLogRepository.class);
        adapter = new AuditLogRepositoryAdapter(auditLogRepository);
    }

    @Test
    void save_ShouldConvertAndReturnSavedAuditLog() {
        AuditLog auditLog = buildAuditLog(1L);
        AuditLogEntity entity = buildAuditLogEntity(1L);

        when(auditLogRepository.save(any(AuditLogEntity.class))).thenReturn(entity);

        AuditLog result = adapter.save(auditLog);

        assertNotNull(result);
        assertEquals(auditLog.getId(), result.getId());
        verify(auditLogRepository).save(any(AuditLogEntity.class));
    }

    @Test
    void findAll_ShouldReturnListOfAuditLogs() {
        AuditLogEntity entity1 = buildAuditLogEntity(1L);
        AuditLogEntity entity2 = buildAuditLogEntity(2L);

        when(auditLogRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        List<AuditLog> result = adapter.findAll();

        assertEquals(2, result.size());
        assertEquals(entity1.getId(), result.get(0).getId());
        assertEquals(entity2.getId(), result.get(1).getId());
        verify(auditLogRepository).findAll();
    }

    @Test
    void findByUserId_ShouldReturnListOfAuditLogs() {
        Long userId = 10L;
        AuditLogEntity entity = buildAuditLogEntity(1L);
        when(auditLogRepository.findByUserId(userId)).thenReturn(Collections.singletonList(entity));

        List<AuditLog> result = adapter.findByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
        verify(auditLogRepository).findByUserId(userId);
    }

    @Test
    void findByAction_ShouldReturnListOfAuditLogs() {
        String action = "LOGIN";
        AuditLogEntity entity = buildAuditLogEntity(1L);
        when(auditLogRepository.findByAction(action)).thenReturn(Collections.singletonList(entity));

        List<AuditLog> result = adapter.findByAction(action);

        assertEquals(1, result.size());
        assertEquals(entity.getAction(), result.get(0).getAction());
        verify(auditLogRepository).findByAction(action);
    }

    @Test
    void findByTimestampBetween_ShouldReturnListOfAuditLogs() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        AuditLogEntity entity = buildAuditLogEntity(1L);
        when(auditLogRepository.findByTimestampBetween(start, end)).thenReturn(Collections.singletonList(entity));

        List<AuditLog> result = adapter.findByTimestampBetween(start, end);

        assertEquals(1, result.size());
        assertEquals(entity.getTimestamp(), result.get(0).getTimestamp());
        verify(auditLogRepository).findByTimestampBetween(start, end);
    }

    private AuditLog buildAuditLog(Long id) {
        return AuditLog.builder()
                .id(id)
                .userId(10L)
                .username("user")
                .action("LOGIN")
                .entity("BOOK")
                .entityId(100L)
                .details("details")
                .ipAddress("127.0.0.1")
                .browser("Chrome")
                .device("PC")
                .operatingSystem("Windows")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private AuditLogEntity buildAuditLogEntity(Long id) {
        return AuditLogEntity.builder()
                .id(id)
                .userId(10L)
                .username("user")
                .action("LOGIN")
                .entity("BOOK")
                .entityId(100L)
                .details("details")
                .ipAddress("127.0.0.1")
                .browser("Chrome")
                .device("PC")
                .operatingSystem("Windows")
                .timestamp(LocalDateTime.now())
                .build();
    }
}