package com.aziz.library.presentation.controller;

import com.aziz.library.application.dto.request.LoginRequest;
import com.aziz.library.application.dto.request.OtpVerificationRequest;
import com.aziz.library.application.dto.request.RegisterRequest;
import com.aziz.library.application.dto.response.ApiResponse;
import com.aziz.library.application.dto.response.LoginResponse;
import com.aziz.library.application.dto.response.UserResponse;
import com.aziz.library.application.mapper.UserMapper;
import com.aziz.library.domain.model.User;
import com.aziz.library.domain.port.in.AuthenticationUseCase;
import com.aziz.library.infrastructure.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account with default VIEWER role")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        log.info("Registration request for username: {}", request.getUsername());
        
        User user = authenticationUseCase.register(
            request.getFullname(),
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );
        
        UserResponse response = userMapper.toResponse(user);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login with username/email and password. Returns OTP requirement.")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Login request for: {}", request.getIdentifier());
        
        String ipAddress = getClientIpAddress(httpRequest);
        String result = authenticationUseCase.login(
            request.getIdentifier(),
            request.getPassword(),
            ipAddress
        );
        
        // Parse result (format: "OTP_REQUIRED:userId")
        if (result.startsWith("OTP_REQUIRED:")) {
            Long userId = Long.parseLong(result.split(":")[1]);
            
            LoginResponse response = LoginResponse.builder()
                    .userId(userId)
                    .otpRequired(true)
                    .message("OTP sent to your email. Please verify to complete login.")
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success("OTP required", response));
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Login failed", null));
    }
    
    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verify OTP code and receive JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request) {
        
        log.info("OTP verification for user: {}", request.getUserId());
        
        String token = authenticationUseCase.verifyOtpAndGenerateToken(
            request.getUserId(),
            request.getOtpCode()
        );
        
        // Parse token result (format: "JWT_TOKEN_PLACEHOLDER:userId:role")
        String[] parts = token.split(":");
        String username = parts[1]; // This should be username, not userId
        String role = parts[2];
        
        String jwtToken = jwtTokenProvider.generateTokenWithRole(username, role);
        
        LoginResponse response = LoginResponse.builder()
                .accessToken(jwtToken)
                .tokenType("Bearer")
                .otpRequired(false)
                .message("Login successful")
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }
    
    @PostMapping("/resend-otp/{userId}")
    @Operation(summary = "Resend OTP", description = "Resend OTP code to user's email")
    public ResponseEntity<ApiResponse<String>> resendOtp(@PathVariable Long userId) {
        
        log.info("Resend OTP request for user: {}", userId);
        
        authenticationUseCase.resendOtp(userId);
        
        return ResponseEntity.ok(
            ApiResponse.success("OTP resent successfully", "Check your email for new OTP code")
        );
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

}
