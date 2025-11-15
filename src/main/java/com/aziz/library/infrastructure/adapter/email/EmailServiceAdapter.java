package com.aziz.library.infrastructure.adapter.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.aziz.library.domain.port.out.EmailServicePort;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailServiceAdapter implements EmailServicePort {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.name}")
    private String appName;
    
    @Override
    @Async
    public void sendOtpEmail(String to, String fullname, String otpCode) {
        log.info("Sending OTP email to: {}", to);
        
        String subject = "Your OTP Code - " + appName;
        String htmlContent = buildOtpEmailTemplate(fullname, otpCode);
        
        sendEmail(to, subject, htmlContent);
    }
    
    @Override
    @Async
    public void sendWelcomeEmail(String to, String fullname) {
        log.info("Sending welcome email to: {}", to);
        
        String subject = "Welcome to " + appName;
        String htmlContent = buildWelcomeEmailTemplate(fullname);
        
        sendEmail(to, subject, htmlContent);
    }
    
    @Override
    @Async
    public void sendAccountLockedEmail(String to, String fullname, int minutes) {
        log.info("Sending account locked email to: {}", to);
        
        String subject = "Account Locked - " + appName;
        String htmlContent = buildAccountLockedEmailTemplate(fullname, minutes);
        
        sendEmail(to, subject, htmlContent);
    }
    
    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }
    
    private String buildOtpEmailTemplate(String fullname, String otpCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 30px; background: #f9f9f9; }
                    .otp-code { font-size: 32px; font-weight: bold; color: #4CAF50; 
                                text-align: center; padding: 20px; background: white; 
                                border-radius: 5px; margin: 20px 0; letter-spacing: 5px; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>You have requested to log in to your account. Please use the following OTP code:</p>
                        <div class="otp-code">%s</div>
                        <p><strong>This code will expire in 5 minutes.</strong></p>
                        <p>If you didn't request this code, please ignore this email or contact support.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2024 %s. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(appName, fullname, otpCode, appName);
    }
    
    private String buildWelcomeEmailTemplate(String fullname) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #2196F3; color: white; padding: 20px; text-align: center; }
                    .content { padding: 30px; background: #f9f9f9; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to %s!</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>Thank you for registering with %s!</p>
                        <p>Your account has been successfully created with the VIEWER role.</p>
                        <p>You can now log in and start exploring our library management system.</p>
                        <p>If you have any questions, feel free to contact our support team.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2024 %s. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(appName, fullname, appName, appName);
    }
    
    private String buildAccountLockedEmailTemplate(String fullname, int minutes) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #f44336; color: white; padding: 20px; text-align: center; }
                    .content { padding: 30px; background: #f9f9f9; }
                    .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Account Locked</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <div class="warning">
                            <strong>Security Alert:</strong> Your account has been temporarily locked due to 
                            multiple failed login attempts.
                        </div>
                        <p>Your account will be automatically unlocked in <strong>%d minutes</strong>.</p>
                        <p>If you didn't attempt to log in, please contact our support team immediately 
                           as this may indicate unauthorized access attempts.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2024 %s. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullname, minutes, appName);
    }

}
