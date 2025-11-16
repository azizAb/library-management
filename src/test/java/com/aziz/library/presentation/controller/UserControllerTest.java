package com.aziz.library.presentation.controller;

import com.aziz.library.application.dto.response.ApiResponse;
import com.aziz.library.application.dto.response.UserResponse;
import com.aziz.library.application.mapper.UserMapper;
import com.aziz.library.domain.model.Role;
import com.aziz.library.domain.model.User;
import com.aziz.library.domain.port.in.UserManagementUseCase;
import com.aziz.library.infrastructure.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserManagementUseCase userManagementUseCase;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_returnsUserResponse_whenUserExists() {
        Long userId = 1L;
        Long adminId = 100L;
        String adminUsername = "admin";

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setRole(Role.VIEWER);

        UserResponse userResponse = UserResponse.builder()
                .id(userId)
                .username("testuser")
                .role(Role.VIEWER)
                .build();

        when(authentication.getName()).thenReturn(adminUsername);
        when(userDetailsService.getUserIdByUsername(adminUsername)).thenReturn(adminId);
        when(userManagementUseCase.getUserById(userId, adminId)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        ResponseEntity<ApiResponse<UserResponse>> response = userController.getUserById(userId, authentication);

        assertNotNull(response.getBody());
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("User retrieved successfully", response.getBody().getMessage());
        assertEquals(userResponse, response.getBody().getData());

        verify(userManagementUseCase).getUserById(userId, adminId);
        verify(userMapper).toResponse(user);
        verify(userDetailsService).getUserIdByUsername(adminUsername);
    }

    @Test
    void getUserById_throwsException_whenUserNotFound() {
        Long userId = 2L;
        Long adminId = 100L;
        String adminUsername = "admin";

        when(authentication.getName()).thenReturn(adminUsername);
        when(userDetailsService.getUserIdByUsername(adminUsername)).thenReturn(adminId);
        when(userManagementUseCase.getUserById(userId, adminId)).thenThrow(new RuntimeException("User not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userController.getUserById(userId, authentication)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userManagementUseCase).getUserById(userId, adminId);
        verify(userDetailsService).getUserIdByUsername(adminUsername);
    }
}