package com.aziz.library.presentation.advice;

import com.aziz.library.application.dto.response.ApiResponse;
import com.aziz.library.domain.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;
    
    @Test
    void testHandleUserNotFound_ShouldReturn404() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleUserNotFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
    }
    
    @Test
    void testHandleArticleNotFound_ShouldReturn404() {
        ArticleNotFoundException ex = new ArticleNotFoundException("Article not found");
        
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleArticleNotFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    void testHandleUnauthorized_ShouldReturn401() {
        UnauthorizedException ex = new UnauthorizedException("Unauthorized");
        
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleUnauthorized(ex);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void testHandleAccountLocked_ShouldReturn403() {
        AccountLockedException ex = new AccountLockedException("Account locked");
        
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleAccountLocked(ex);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    
    @Test
    void testHandleInvalidOtp_ShouldReturn400() {
        InvalidOtpException ex = new InvalidOtpException("Invalid OTP");
        
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleInvalidOtp(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void testHandleDuplicateResource_ShouldReturn409() {
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate");
        
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleDuplicateResource(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
    
    @Test
    void testHandleValidationExceptions_ShouldReturn400WithErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "error message");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        
        ResponseEntity<ApiResponse<Map<String, String>>> response = 
                exceptionHandler.handleValidationExceptions(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
    }
    
    @Test
    void testHandleGenericException_ShouldReturn500() {
        Exception ex = new Exception("Generic error");
        
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleGenericException(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
