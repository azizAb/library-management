package com.aziz.library.domain.service;

import com.aziz.library.domain.exception.*;
import com.aziz.library.domain.model.*;
import com.aziz.library.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {

   @Mock
    private UserRepositoryPort userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserManagementService userManagementService;
    
    @Test
    void testCreateUser_AsSuperAdmin_ShouldCreateSuccessfully() {
        User admin = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        User newUser = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(newUser);
        
        User result = userManagementService.createUser(newUser, 1L);
        
        assertNotNull(result);
        verify(userRepository).save(any());
    }
    
    @Test
    void testCreateUser_AsNonAdmin_ShouldThrowUnauthorizedException() {
        User editor = User.builder()
                .id(1L)
                .role(Role.EDITOR)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(editor));
        
        assertThrows(UnauthorizedException.class, () -> 
            userManagementService.createUser(new User(), 1L)
        );
    }
    
    @Test
    void testUpdateUser_ShouldUpdateSuccessfully() {
        User admin = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        User existing = User.builder()
                .id(2L)
                .username("olduser")
                .email("old@example.com")
                .build();
        
        User updates = User.builder()
                .fullname("New Name")
                .username("olduser")
                .email("old@example.com")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenReturn(existing);
        
        User result = userManagementService.updateUser(2L, updates, 1L);
        
        assertNotNull(result);
        verify(userRepository).save(any());
    }
    
    @Test
    void testDeleteUser_ShouldDeleteSuccessfully() {
        User admin = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        User targetUser = User.builder()
                .id(2L)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        
        userManagementService.deleteUser(2L, 1L);
        
        verify(userRepository).deleteById(2L);
    }
    
    @Test
    void testDeleteUser_SelfDelete_ShouldThrowUnauthorizedException() {
        User admin = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        
        assertThrows(UnauthorizedException.class, () -> 
            userManagementService.deleteUser(1L, 1L)
        );
    }
    
    @Test
    void testGetAllUsers_ShouldReturnAllUsers() {
        User admin = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        List<User> users = Arrays.asList(
            User.builder().id(1L).build(),
            User.builder().id(2L).build()
        );
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findAll()).thenReturn(users);
        
        List<User> result = userManagementService.getAllUsers(1L);
        
        assertEquals(2, result.size());
    }
    
    @Test
    void testUpdateUserRole_ShouldUpdateSuccessfully() {
        User admin = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        User targetUser = User.builder()
                .id(2L)
                .role(Role.VIEWER)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(userRepository.save(any())).thenReturn(targetUser);
        
        User result = userManagementService.updateUserRole(2L, Role.EDITOR, 1L);
        
        assertNotNull(result);
        verify(userRepository).save(any());
    }
}