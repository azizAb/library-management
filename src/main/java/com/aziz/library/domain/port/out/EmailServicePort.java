package com.aziz.library.domain.port.out;

public interface EmailServicePort {
    void sendOtpEmail(String to, String fullname, String otpCode);
    void sendWelcomeEmail(String to, String fullname);
    void sendAccountLockedEmail(String to, String fullname, int minutes);
}
