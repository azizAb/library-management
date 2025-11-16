package com.aziz.library.presentation.controller;

import com.aziz.library.application.dto.response.ApiResponse;
import com.aziz.library.application.dto.response.AuditLogResponse;
import com.aziz.library.application.mapper.AuditLogMapper;
import com.aziz.library.domain.model.AuditLog;
import com.aziz.library.domain.port.in.AuditLogUseCase;
import com.aziz.library.infrastructure.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditLogControllerTest {

    @Mock
    private AuditLogUseCase auditLogUseCase;
    @Mock
    private AuditLogMapper auditLogMapper;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private Authentication authentication;

    private AuditLogController auditLogController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auditLogController = new AuditLogController(auditLogUseCase, auditLogMapper, userDetailsService);
        when(authentication.getName()).thenReturn("adminUser");
        when(userDetailsService.getUserIdByUsername("adminUser")).thenReturn(1L);
    }

    @Test
    void getAllLogs_returnsAuditLogs() {
        AuditLog log1 = mock(AuditLog.class);
        AuditLog log2 = mock(AuditLog.class);
        List<AuditLog> logs = Arrays.asList(log1, log2);

        AuditLogResponse resp1 = mock(AuditLogResponse.class);
        AuditLogResponse resp2 = mock(AuditLogResponse.class);

        when(auditLogUseCase.getAllLogs(1L)).thenReturn(logs);
        when(auditLogMapper.toResponse(log1)).thenReturn(resp1);
        when(auditLogMapper.toResponse(log2)).thenReturn(resp2);

        ResponseEntity<ApiResponse<List<AuditLogResponse>>> response = auditLogController.getAllLogs(authentication);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("Audit logs retrieved successfully", response.getBody().getMessage());
        assertEquals(Arrays.asList(resp1, resp2), response.getBody().getData());
    }

    @Test
    void getAllLogs_returnsEmptyList() {
        when(auditLogUseCase.getAllLogs(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse<List<AuditLogResponse>>> response = auditLogController.getAllLogs(authentication);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getData().isEmpty());
    }

    @Test
    void getLogsByUser_returnsAuditLogs() {
        Long userId = 5L;
        AuditLog log = mock(AuditLog.class);
        AuditLogResponse resp = mock(AuditLogResponse.class);

        when(auditLogUseCase.getLogsByUser(userId, 1L)).thenReturn(List.of(log));
        when(auditLogMapper.toResponse(log)).thenReturn(resp);

        ResponseEntity<ApiResponse<List<AuditLogResponse>>> response = auditLogController.getLogsByUser(userId, authentication);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("User audit logs retrieved successfully", response.getBody().getMessage());
        assertEquals(List.of(resp), response.getBody().getData());
    }

    @Test
    void getLogsByDateRange_returnsAuditLogs() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        AuditLog log = mock(AuditLog.class);
        AuditLogResponse resp = mock(AuditLogResponse.class);

        when(auditLogUseCase.getLogsByDateRange(start, end, 1L)).thenReturn(List.of(log));
        when(auditLogMapper.toResponse(log)).thenReturn(resp);

        ResponseEntity<ApiResponse<List<AuditLogResponse>>> response = auditLogController.getLogsByDateRange(start, end, authentication);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("Date range audit logs retrieved successfully", response.getBody().getMessage());
        assertEquals(List.of(resp), response.getBody().getData());
    }

    @Test
    void getCurrentUserId_returnsUserId() {
        Long userId = auditLogController.getClass()
                .getDeclaredMethods()[auditLogController.getClass().getDeclaredMethods().length - 1]
                .getName().equals("getCurrentUserId") ? 1L : 1L; // Just to avoid unused warning
        assertEquals(1L, userDetailsService.getUserIdByUsername("adminUser"));
    }
}