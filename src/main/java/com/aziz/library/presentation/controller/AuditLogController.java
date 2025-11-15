package com.aziz.library.presentation.controller;

import com.aziz.library.application.dto.response.ApiResponse;
import com.aziz.library.application.dto.response.AuditLogResponse;
import com.aziz.library.application.mapper.AuditLogMapper;
import com.aziz.library.domain.model.AuditLog;
import com.aziz.library.domain.port.in.AuditLogUseCase;
import com.aziz.library.infrastructure.security.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Audit Logs", description = "Audit log endpoints (SUPER_ADMIN only)")
public class AuditLogController {

    private final AuditLogUseCase auditLogUseCase;
    private final AuditLogMapper auditLogMapper;
    private final CustomUserDetailsService userDetailsService;
    
    @GetMapping
    @Operation(summary = "Get all audit logs", description = "Retrieve all audit logs (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAllLogs(
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.debug("Get all audit logs by admin: {}", currentUserId);
        
        List<AuditLog> logs = auditLogUseCase.getAllLogs(currentUserId);
        List<AuditLogResponse> responses = logs.stream()
                .map(auditLogMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Audit logs retrieved successfully", responses)
        );
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get logs by user", description = "Retrieve audit logs for a specific user (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByUser(
            @PathVariable Long userId,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.debug("Get audit logs for user {} by admin: {}", userId, currentUserId);
        
        List<AuditLog> logs = auditLogUseCase.getLogsByUser(userId, currentUserId);
        List<AuditLogResponse> responses = logs.stream()
                .map(auditLogMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("User audit logs retrieved successfully", responses)
        );
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get logs by date range", description = "Retrieve audit logs within a date range (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.debug("Get audit logs from {} to {} by admin: {}", start, end, currentUserId);
        
        List<AuditLog> logs = auditLogUseCase.getLogsByDateRange(start, end, currentUserId);
        List<AuditLogResponse> responses = logs.stream()
                .map(auditLogMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Date range audit logs retrieved successfully", responses)
        );
    }
    
    private Long getCurrentUserId(Authentication authentication) {
        String username = authentication.getName();
        return userDetailsService.getUserIdByUsername(username);
    }

}
