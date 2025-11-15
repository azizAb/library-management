package com.aziz.library.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.aziz.library.domain.model.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information response")
public class UserResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;
    
    @Schema(description = "User's full name", example = "Abdul Aziz")
    private String fullname;
    
    @Schema(description = "Username", example = "abdulaziz")
    private String username;
    
    @Schema(description = "Email address", example = "abdul.aziz@example.com")
    private String email;
    
    @Schema(description = "User role", example = "VIEWER")
    private Role role;
    
    @Schema(description = "Account enabled status", example = "true")
    private boolean enabled;
    
    @Schema(description = "Account locked status", example = "false")
    private boolean accountLocked;
    
    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

}
