package com.aziz.library.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Audit log entry response")
public class AuditLogResponse {

    @Schema(description = "Log ID", example = "1")
    private Long id;
    
    @Schema(description = "User ID who performed the action", example = "1")
    private Long userId;
    
    @Schema(description = "Username", example = "abdulaziz")
    private String username;
    
    @Schema(description = "Action performed", example = "CREATE_ARTICLE")
    private String action;
    
    @Schema(description = "Entity type", example = "ARTICLE")
    private String entity;
    
    @Schema(description = "Entity ID", example = "5")
    private Long entityId;
    
    @Schema(description = "Additional details")
    private String details;
    
    @Schema(description = "IP address", example = "192.168.1.1")
    private String ipAddress;
    
    @Schema(description = "Browser information", example = "Chrome 120")
    private String browser;
    
    @Schema(description = "Device type", example = "Desktop")
    private String device;
    
    @Schema(description = "Operating system", example = "Windows 10")
    private String operatingSystem;
    
    @Schema(description = "Timestamp of the action")
    private LocalDateTime timestamp;

}
