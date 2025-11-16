package com.aziz.library.domain.service;

import com.aziz.library.domain.exception.*;
import com.aziz.library.domain.model.*;
import com.aziz.library.domain.port.out.*;
import com.aziz.library.domain.service.AutheticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    
    @Mock
    private OtpRepositoryPort otpRepository;
    
    @Mock
    private LoginAttemptRepositoryPort loginAttemptRepository;
    
    @Mock
    private EmailServicePort emailService;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AutheticationService authenticationService;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "otpExpirationSeconds", 300);
        ReflectionTestUtils.setField(authenticationService, "otpLength", 6);
        ReflectionTestUtils.setField(authenticationService, "maxLoginAttempts", 5);
        ReflectionTestUtils.setField(authenticationService, "lockDurationSeconds", 1800);
        ReflectionTestUtils.setField(authenticationService, "attemptWindowSeconds", 600);
    }
    
    @Test
    void testRegister_WithValidData_ShouldCreateUser() {
        String fullname = "abdul aziz";
        String username = "abdulaziz";
        String email = "abdulaziz@example.com";
        String password = "password123";
        
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User user = i.getArgument(0);
            user.setId(1L);
            return user;
        });
        
        User result = authenticationService.register(fullname, username, email, password);
        
        assertNotNull(result);
        assertEquals(fullname, result.getFullname());
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals(Role.VIEWER, result.getRole());
        assertTrue(result.isEnabled());
        assertFalse(result.isAccountLocked());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testRegister_WithExistingUsername_ShouldThrowException() {
        when(userRepository.existsByUsername("abdulaziz")).thenReturn(true);
        
        assertThrows(DuplicateResourceException.class, () -> 
            authenticationService.register("aziz", "abdulaziz", "abdulaziz@example.com", "pass123")
        );
        
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void testRegister_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByUsername("abdulaziz")).thenReturn(false);
        when(userRepository.existsByEmail("abdulaziz@example.com")).thenReturn(true);
        
        assertThrows(DuplicateResourceException.class, () -> 
            authenticationService.register("aziz", "abdulaziz", "abdulaziz@example.com", "pass123")
        );
    }
    
    @Test
    void testLogin_WithValidCredentials_ShouldReturnOtpRequired() {
        String identifier = "abdulaziz";
        String password = "password123";
        String ipAddress = "127.0.0.1";
        
        User user = User.builder()
                .id(1L)
                .username("abdulaziz")
                .email("abdulaziz@example.com")
                .password("encodedPassword")
                .role(Role.VIEWER)
                .enabled(true)
                .accountLocked(false)
                .build();
        
        when(loginAttemptRepository.countFailedAttempts(anyString(), any(LocalDateTime.class)))
                .thenReturn(0);
        when(userRepository.findByUsernameOrEmail(identifier))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        
        String result = authenticationService.login(identifier, password, ipAddress);
        
        assertTrue(result.startsWith("OTP_REQUIRED:"));
        verify(otpRepository).save(any());
        verify(loginAttemptRepository).save(any());
    }
    
    @Test
    void testLogin_WithInvalidCredentials_ShouldThrowException() {
        String identifier = "abdulaziz";
        String password = "wrongpassword";
        
        when(loginAttemptRepository.countFailedAttempts(anyString(), any(LocalDateTime.class)))
                .thenReturn(0);
        when(userRepository.findByUsernameOrEmail(identifier))
                .thenReturn(Optional.empty());
        
        assertThrows(UnauthorizedException.class, () -> 
            authenticationService.login(identifier, password, "127.0.0.1")
        );
        
        verify(loginAttemptRepository).save(any());
    }
    
    @Test
    void testLogin_WithMaxFailedAttempts_ShouldThrowAccountLockedException() {
        when(loginAttemptRepository.countFailedAttempts(anyString(), any(LocalDateTime.class)))
                .thenReturn(5);
        
        assertThrows(AccountLockedException.class, () -> 
            authenticationService.login("abdulaziz", "password", "127.0.0.1")
        );
    }
    
    @Test
    void testLogin_WithWrongPassword_ShouldRecordFailedAttempt() {
        User user = User.builder()
                .id(1L)
                .username("abdulaziz")
                .password("encodedPassword")
                .enabled(true)
                .accountLocked(false)
                .build();
        
        when(loginAttemptRepository.countFailedAttempts(anyString(), any(LocalDateTime.class)))
                .thenReturn(0, 1);
        when(userRepository.findByUsernameOrEmail(anyString()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        assertThrows(UnauthorizedException.class, () -> 
            authenticationService.login("abdulaziz", "wrong", "127.0.0.1")
        );
        
        verify(loginAttemptRepository, times(1)).save(any());
    }
    
    @Test
    void testVerifyOtp_WithValidOtp_ShouldReturnToken() {
        Long userId = 1L;
        String otpCode = "123456";
        
        User user = User.builder()
                .id(userId)
                .username("abdulaziz")
                .role(Role.VIEWER)
                .build();
        
        OtpToken otpToken = OtpToken.builder()
                .userId(userId)
                .token(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(otpRepository.findLatestByUserId(userId)).thenReturn(Optional.of(otpToken));
        
        String result = authenticationService.verifyOtpAndGenerateToken(userId, otpCode);
        
        assertTrue(result.contains("JWT_TOKEN_PLACEHOLDER"));
        verify(otpRepository).save(argThat(otp -> otp.isUsed()));
    }
    
    @Test
    void testVerifyOtp_WithExpiredOtp_ShouldThrowException() {
        Long userId = 1L;
        
        User user = User.builder().id(userId).build();
        OtpToken otpToken = OtpToken.builder()
                .userId(userId)
                .token("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(otpRepository.findLatestByUserId(userId)).thenReturn(Optional.of(otpToken));
        
        assertThrows(InvalidOtpException.class, () -> 
            authenticationService.verifyOtpAndGenerateToken(userId, "123456")
        );
    }
    
    @Test
    void testVerifyOtp_WithUsedOtp_ShouldThrowException() {
        Long userId = 1L;
        
        User user = User.builder().id(userId).build();
        OtpToken otpToken = OtpToken.builder()
                .userId(userId)
                .token("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(true)
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(otpRepository.findLatestByUserId(userId)).thenReturn(Optional.of(otpToken));
        
        assertThrows(InvalidOtpException.class, () -> 
            authenticationService.verifyOtpAndGenerateToken(userId, "123456")
        );
    }
    
    @Test
    void testVerifyOtp_WithInvalidOtp_ShouldThrowException() {
        Long userId = 1L;
        
        User user = User.builder().id(userId).build();
        OtpToken otpToken = OtpToken.builder()
                .userId(userId)
                .token("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(otpRepository.findLatestByUserId(userId)).thenReturn(Optional.of(otpToken));
        
        assertThrows(InvalidOtpException.class, () -> 
            authenticationService.verifyOtpAndGenerateToken(userId, "999999")
        );
    }
    
    @Test
    void testResendOtp_ShouldDeleteOldAndCreateNew() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .fullname("Test User")
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        authenticationService.resendOtp(userId);
        
        verify(otpRepository).deleteByUserId(userId);
        verify(otpRepository).save(any(OtpToken.class));
    }

}
