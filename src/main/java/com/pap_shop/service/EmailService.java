package com.pap_shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    /**
     * Sends a password reset email to the user.
     *
     * @param to the recipient email address
     * @param resetToken the reset token for password reset
     */
    public void sendResetPasswordEmail(String to, String resetCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset Your Password - PAP Shop");
        message.setText("Your password reset code is:\n\n" +
                       resetCode + "\n\n" +
                       "Enter this code on the reset password page to continue.\n" +
                       "This code will expire in 15 minutes.\n\n" +
                       "If you didn't request this, please ignore this email.");
        mailSender.send(message);
    }
}