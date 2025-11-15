package com.aziz.library.presentation.controller;

import com.aziz.library.application.dto.request.UserRequest;
import com.aziz.library.application.dto.response.ApiResponse;
import com.aziz.library.application.dto.response.UserResponse;
import com.aziz.library.application.mapper.UserMapper;
import com.aziz.library.domain.model.Role;
import com.aziz.library.domain.model.User;
import com.aziz.library.domain.port.in.UserManagementUseCase;
import com.aziz.library.infrastructure.security.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "User Management", description = "User management endpoints (SUPER_ADMIN only)")
public class UserController {

    private final UserManagementUseCase userManagementUseCase;
    private final UserMapper userMapper;
    private final CustomUserDetailsService userDetailsService;
    
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRequest request,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.info("Create user request by admin: {}", currentUserId);
        
        User user = userMapper.toDomain(request);
        User createdUser = userManagementUseCase.createUser(user, currentUserId);
        UserResponse response = userMapper.toResponse(createdUser);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.debug("Get all users request by admin: {}", currentUserId);
        
        List<User> users = userManagementUseCase.getAllUsers(currentUserId);
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", responses));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.debug("Get user {} by admin: {}", id, currentUserId);
        
        User user = userManagementUseCase.getUserById(id, currentUserId);
        UserResponse response = userMapper.toResponse(user);
        
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.info("Update user {} by admin: {}", id, currentUserId);
        
        User user = userMapper.toDomain(request);
        User updatedUser = userManagementUseCase.updateUser(id, user, currentUserId);
        UserResponse response = userMapper.toResponse(updatedUser);
        
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.info("Delete user {} by admin: {}", id, currentUserId);
        
        userManagementUseCase.deleteUser(id, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
    
    @PatchMapping("/{id}/role")
    @Operation(summary = "Update user role", description = "Update a user's role (SUPER_ADMIN only)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role role,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.info("Update role for user {} to {} by admin: {}", id, role, currentUserId);
        
        User updatedUser = userManagementUseCase.updateUserRole(id, role, currentUserId);
        UserResponse response = userMapper.toResponse(updatedUser);
        
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", response));
    }
    
    private Long getCurrentUserId(Authentication authentication) {
        String username = authentication.getName();
        return userDetailsService.getUserIdByUsername(username);
    }
    
}
