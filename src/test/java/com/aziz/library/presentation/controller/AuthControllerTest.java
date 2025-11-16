package com.aziz.library.presentation.controller;

import com.aziz.library.application.dto.request.LoginRequest;
import com.aziz.library.application.dto.response.ApiResponse;
import com.aziz.library.application.dto.response.LoginResponse;
import com.aziz.library.domain.port.in.AuthenticationUseCase;
import com.aziz.library.infrastructure.security.jwt.JwtTokenProvider;
import com.aziz.library.application.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthenticationUseCase authenticationUseCase;
    private JwtTokenProvider jwtTokenProvider;
    private UserMapper userMapper;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authenticationUseCase = mock(AuthenticationUseCase.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        userMapper = mock(UserMapper.class);
        authController = new AuthController(authenticationUseCase, jwtTokenProvider, userMapper);
    }

    @Test
    void login_shouldReturnOtpRequired_whenOtpIsRequired() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("user@example.com");
        loginRequest.setPassword("password123");

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        when(authenticationUseCase.login(eq("user@example.com"), eq("password123"), eq("127.0.0.1")))
                .thenReturn("OTP_REQUIRED:42");

        // Act
        ResponseEntity<ApiResponse<LoginResponse>> response = authController.login(loginRequest, httpRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<LoginResponse> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("OTP required", apiResponse.getMessage());
        assertNotNull(apiResponse.getData());
        assertEquals(42L, apiResponse.getData().getUserId());
        assertTrue(apiResponse.getData().isOtpRequired());
        assertEquals("OTP sent to your email. Please verify to complete login.", apiResponse.getData().getMessage());
    }

    @Test
    void login_shouldReturnInternalServerError_whenLoginFails() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("user@example.com");
        loginRequest.setPassword("wrongpassword");

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        when(authenticationUseCase.login(eq("user@example.com"), eq("wrongpassword"), eq("127.0.0.1")))
                .thenReturn("LOGIN_FAILED");

        // Act
        ResponseEntity<ApiResponse<LoginResponse>> response = authController.login(loginRequest, httpRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse<LoginResponse> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Login failed", apiResponse.getMessage());
        assertNull(apiResponse.getData());
    }
}