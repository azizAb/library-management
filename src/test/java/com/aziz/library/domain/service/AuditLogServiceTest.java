package com.aziz.library.domain.service;

import com.aziz.library.domain.exception.UnauthorizedException;
import com.aziz.library.domain.model.*;
import com.aziz.library.domain.port.out.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditLogServiceTest {

    @Mock
    private AuditLogRepositoryPort auditLogRepository;
    
    @Mock
    private UserRepositoryPort userRepository;
    
    @InjectMocks
    private AuditLogService auditLogService;
    
    @Test
    void testLogAction_ShouldSaveAuditLog() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        auditLogService.logAction(1L, "CREATE", "ARTICLE", 1L, 
                                  "Created article", "127.0.0.1", "Mozilla/5.0");
        
        verify(auditLogRepository).save(any(AuditLog.class));
    }
    
    @Test
    void testGetAllLogs_AsSuperAdmin_ShouldReturnAllLogs() {
        User admin = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        List<AuditLog> logs = Arrays.asList(
            AuditLog.builder().id(1L).build(),
            AuditLog.builder().id(2L).build()
        );
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(auditLogRepository.findAll()).thenReturn(logs);
        
        List<AuditLog> result = auditLogService.getAllLogs(1L);
        
        assertEquals(2, result.size());
    }
    
    @Test
    void testGetAllLogs_AsNonAdmin_ShouldThrowUnauthorizedException() {
        User editor = User.builder()
                .id(1L)
                .role(Role.EDITOR)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(editor));
        
        assertThrows(UnauthorizedException.class, () -> 
            auditLogService.getAllLogs(1L)
        );
    }
    
    @Test
    void testGetLogsByUser_ShouldReturnUserLogs() {
        User admin = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        List<AuditLog> logs = Arrays.asList(
            AuditLog.builder().userId(2L).build()
        );
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(auditLogRepository.findByUserId(2L)).thenReturn(logs);
        
        List<AuditLog> result = auditLogService.getLogsByUser(2L, 1L);
        
        assertEquals(1, result.size());
    }
    
    @Test
    void testGetLogsByDateRange_ShouldReturnLogsInRange() {
        User admin = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        
        List<AuditLog> logs = Arrays.asList(
            AuditLog.builder().id(1L).build()
        );
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(auditLogRepository.findByTimestampBetween(start, end)).thenReturn(logs);
        
        List<AuditLog> result = auditLogService.getLogsByDateRange(start, end, 1L);
        
        assertEquals(1, result.size());
    }

}
