package com.aziz.library.domain.port.in;

import com.aziz.library.domain.model.User;

public interface AuthenticationUseCase {
    User register(String fullname, String username, String email, String password);
    String login(String identifier, String password, String ipAddress);
    String verifyOtpAndGenerateToken(Long userId, String otpCode);
    void resendOtp(Long userId);
    boolean validateToken(String token);
    Long getUserIdFromToken(String token);
}
