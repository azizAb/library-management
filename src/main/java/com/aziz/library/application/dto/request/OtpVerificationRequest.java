package com.aziz.library.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OTP verification request")
public class OtpVerificationRequest {

    @NotNull(message = "User ID is required")
    @Schema(description = "User ID", example = "1")
    private Long userId;
    
    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
    @Schema(description = "6-digit OTP code", example = "123456")
    private String otpCode;

}
