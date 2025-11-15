package com.aziz.library.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login response with JWT token or OTP requirement")
public class LoginResponse {

    @Schema(description = "JWT access token (if OTP verified)")
    private String accessToken;
    
    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "User ID (for OTP verification)")
    private Long userId;
    
    @Schema(description = "Indicates if OTP is required", example = "true")
    private boolean otpRequired;
    
    @Schema(description = "Additional message")
    private String message;
    
}
