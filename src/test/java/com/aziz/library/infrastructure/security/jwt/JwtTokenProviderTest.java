package com.aziz.library.infrastructure.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    
    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", 
                "YourSuperSecretKeyForJWTTokenGenerationMustBeLongEnough");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 86400000L);
    }
    
    @Test
    void testGenerateTokenFromUsername_ShouldReturnToken() {
        String token = jwtTokenProvider.generateTokenFromUsername("testuser");
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
    
    @Test
    void testGenerateTokenWithRole_ShouldReturnTokenWithRole() {
        String token = jwtTokenProvider.generateTokenWithRole("testuser", "VIEWER");
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
    
    @Test
    void testGetUsernameFromToken_ShouldReturnUsername() {
        String token = jwtTokenProvider.generateTokenFromUsername("testuser");
        
        String username = jwtTokenProvider.getUsernameFromToken(token);
        
        assertEquals("testuser", username);
    }
    
    @Test
    void testValidateToken_WithValidToken_ShouldReturnTrue() {
        String token = jwtTokenProvider.generateTokenFromUsername("testuser");
        
        boolean isValid = jwtTokenProvider.validateToken(token);
        
        assertTrue(isValid);
    }
    
    @Test
    void testValidateToken_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid.token.here";
        
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);
        
        assertFalse(isValid);
    }
    
    @Test
    void testValidateToken_WithNullToken_ShouldReturnFalse() {
        boolean isValid = jwtTokenProvider.validateToken(null);
        
        assertFalse(isValid);
    }

}
