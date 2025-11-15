package com.aziz.library.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    private Long id;
    private Long userId;
    private String username;
    private String action;
    private String entity;
    private Long entityId;
    private String details;
    private String ipAddress;
    private String browser;
    private String device;
    private String operatingSystem;
    private LocalDateTime timestamp;
}
