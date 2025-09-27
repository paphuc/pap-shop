package com.pap_shop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendResetPasswordEmail_shouldSendEmailWithCorrectContent() {
        String email = "test@example.com";
        String resetCode = "ABC123";
        
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        
        emailService.sendResetPasswordEmail(email, resetCode);
        
        verify(mailSender).send(messageCaptor.capture());
        
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(email, sentMessage.getTo()[0]);
        assertEquals("Reset Your Password - PAP Shop", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(resetCode));
        assertTrue(sentMessage.getText().contains("Your password reset code is:"));
    }
}