package com.aziz.library.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aziz.library.domain.exception.DuplicateResourceException;
import com.aziz.library.domain.exception.UnauthorizedException;
import com.aziz.library.domain.exception.UserNotFoundException;
import com.aziz.library.domain.model.Role;
import com.aziz.library.domain.model.User;
import com.aziz.library.domain.port.in.UserManagementUseCase;
import com.aziz.library.domain.port.out.UserRepositoryPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService implements UserManagementUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public User createUser(User user, Long currentUserId) {
        checkSuperAdminAccess(currentUserId);
        
        log.info("Creating new user: {}", user.getUsername());
        
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    @Override
    @Transactional
    public User updateUser(Long id, User user, Long currentUserId) {
        checkSuperAdminAccess(currentUserId);
        
        log.info("Updating user: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Check for username uniqueness
        if (!existingUser.getUsername().equals(user.getUsername()) && 
            userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        
        // Check for email uniqueness
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        existingUser.setFullname(user.getFullname());
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully: {}", updatedUser.getUsername());
        
        return updatedUser;
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id, Long currentUserId) {
        checkSuperAdminAccess(currentUserId);
        
        log.info("Deleting user: {}", id);
        
        if (!userRepository.findById(id).isPresent()) {
            throw new UserNotFoundException("User not found");
        }
        
        // Prevent deleting yourself
        if (id.equals(currentUserId)) {
            throw new UnauthorizedException("Cannot delete your own account");
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully: {}", id);
    }
    
    @Override
    public User getUserById(Long id, Long currentUserId) {
        checkSuperAdminAccess(currentUserId);
        
        log.debug("Getting user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    
    @Override
    public List<User> getAllUsers(Long currentUserId) {
        checkSuperAdminAccess(currentUserId);
        
        log.debug("Getting all users");
        return userRepository.findAll();
    }
    
    @Override
    @Transactional
    public User updateUserRole(Long id, Role role, Long currentUserId) {
        checkSuperAdminAccess(currentUserId);
        
        log.info("Updating role for user: {} to {}", id, role);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        user.setRole(role);
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        log.info("User role updated successfully");
        
        return updatedUser;
    }
    
    private void checkSuperAdminAccess(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        if (user.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException("Only SUPER_ADMIN can manage users");
        }
    }
    
}
