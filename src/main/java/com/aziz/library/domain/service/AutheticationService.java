package com.aziz.library.domain.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aziz.library.domain.exception.AccountLockedException;
import com.aziz.library.domain.exception.DuplicateResourceException;
import com.aziz.library.domain.exception.InvalidOtpException;
import com.aziz.library.domain.exception.UnauthorizedException;
import com.aziz.library.domain.exception.UserNotFoundException;
import com.aziz.library.domain.model.LoginAttempt;
import com.aziz.library.domain.model.OtpToken;
import com.aziz.library.domain.model.Role;
import com.aziz.library.domain.model.User;
import com.aziz.library.domain.port.in.AuthenticationUseCase;
import com.aziz.library.domain.port.out.EmailServicePort;
import com.aziz.library.domain.port.out.LoginAttemptRepositoryPort;
import com.aziz.library.domain.port.out.OtpRepositoryPort;
import com.aziz.library.domain.port.out.UserRepositoryPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutheticationService implements AuthenticationUseCase {

    private final UserRepositoryPort userRepository;
    private final OtpRepositoryPort otpRepository;
    private final LoginAttemptRepositoryPort loginAttemptRepository;
    private final EmailServicePort emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.otp.expiration}")
    private int otpExpirationSeconds;
    
    @Value("${security.otp.length}")
    private int otpLength;
    
    @Value("${security.login.max-attempts}")
    private int maxLoginAttempts;
    
    @Value("${security.login.lock-duration}")
    private int lockDurationSeconds;
    
    @Value("${security.login.attempt-window}")
    private int attemptWindowSeconds;
    
    @Override
    @Transactional
    public User register(String fullname, String username, String email, String password) {
        log.info("Registering new user: {}", username);
        
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        User user = User.builder()
                .fullname(fullname)
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.VIEWER)
                .enabled(true)
                .accountLocked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(email, fullname);
        
        log.info("User registered successfully: {}", username);
        return savedUser;
    }
    
    @Override
    @Transactional
    public String login(String identifier, String password, String ipAddress) {
        log.info("Login attempt for: {}", identifier);
        
        // Check for too many failed attempts
        LocalDateTime windowStart = LocalDateTime.now().minusSeconds(attemptWindowSeconds);
        int failedAttempts = loginAttemptRepository.countFailedAttempts(identifier, windowStart);
        
        if (failedAttempts >= maxLoginAttempts) {
            throw new AccountLockedException("Account is locked due to too many failed login attempts");
        }
        
        // Find user
        User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> {
                    recordLoginAttempt(identifier, ipAddress, false);
                    return new UnauthorizedException("Invalid credentials");
                });
        
        // Check if account is locked
        if (user.isAccountLocked() && user.getLockUntil() != null) {
            if (LocalDateTime.now().isBefore(user.getLockUntil())) {
                throw new AccountLockedException("Account is locked until " + user.getLockUntil());
            } else {
                // Unlock account
                user.setAccountLocked(false);
                user.setLockUntil(null);
                userRepository.save(user);
            }
        }
        
        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            recordLoginAttempt(identifier, ipAddress, false);
            
            // Check again for max attempts
            failedAttempts = loginAttemptRepository.countFailedAttempts(identifier, windowStart);
            if (failedAttempts >= maxLoginAttempts) {
                lockAccount(user);
            }
            
            throw new UnauthorizedException("Invalid credentials");
        }
        
        recordLoginAttempt(identifier, ipAddress, true);
        
        // Generate and send OTP
        String otpCode = generateOtp();
        OtpToken otpToken = OtpToken.builder()
                .userId(user.getId())
                .token(otpCode)
                .expiresAt(LocalDateTime.now().plusSeconds(otpExpirationSeconds))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        otpRepository.save(otpToken);
        emailService.sendOtpEmail(user.getEmail(), user.getFullname(), otpCode);
        
        log.info("OTP sent to user: {}", identifier);
        return "OTP_REQUIRED:" + user.getId();
    }
    
    @Override
    @Transactional
    public String verifyOtpAndGenerateToken(Long userId, String otpCode) {
        log.info("Verifying OTP for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        OtpToken otpToken = otpRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new InvalidOtpException("No OTP found for user"));
        
        if (otpToken.isUsed()) {
            throw new InvalidOtpException("OTP already used");
        }
        
        if (LocalDateTime.now().isAfter(otpToken.getExpiresAt())) {
            throw new InvalidOtpException("OTP has expired");
        }
        
        if (!otpToken.getToken().equals(otpCode)) {
            throw new InvalidOtpException("Invalid OTP code");
        }
        
        // Mark OTP as used
        otpToken.setUsed(true);
        otpRepository.save(otpToken);
        
        // Generate JWT token (will be implemented in JwtTokenProvider)
        log.info("OTP verified successfully for user: {}", userId);
        return "JWT_TOKEN_PLACEHOLDER:" + userId + ":" + user.getRole();
    }
    
    @Override
    public void resendOtp(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Delete old OTP
        otpRepository.deleteByUserId(userId);
        
        // Generate new OTP
        String otpCode = generateOtp();
        OtpToken otpToken = OtpToken.builder()
                .userId(user.getId())
                .token(otpCode)
                .expiresAt(LocalDateTime.now().plusSeconds(otpExpirationSeconds))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        otpRepository.save(otpToken);
        emailService.sendOtpEmail(user.getEmail(), user.getFullname(), otpCode);
        
        log.info("OTP resent to user: {}", userId);
    }
    
    @Override
    public boolean validateToken(String token) {
        // Will be implemented with JWT
        return false;
    }
    
    @Override
    public Long getUserIdFromToken(String token) {
        // Will be implemented with JWT
        return null;
    }
    
    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }
    
    private void recordLoginAttempt(String identifier, String ipAddress, boolean successful) {
        LoginAttempt attempt = LoginAttempt.builder()
                .identifier(identifier)
                .ipAddress(ipAddress)
                .successful(successful)
                .attemptTime(LocalDateTime.now())
                .build();
        
        loginAttemptRepository.save(attempt);
    }
    
    private void lockAccount(User user) {
        user.setAccountLocked(true);
        user.setLockUntil(LocalDateTime.now().plusSeconds(lockDurationSeconds));
        userRepository.save(user);
        
        emailService.sendAccountLockedEmail(
            user.getEmail(), 
            user.getFullname(), 
            lockDurationSeconds / 60
        );
        
        log.warn("Account locked for user: {}", user.getUsername());
    }

}
