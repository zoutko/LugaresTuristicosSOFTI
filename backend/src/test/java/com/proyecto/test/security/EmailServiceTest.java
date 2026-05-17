package com.proyecto.app.security.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService - Pruebas Unitarias")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    // ----------------------------------------------------------------
    // sendRecoveryToken
    // ----------------------------------------------------------------

    @Test
    @DisplayName("sendRecoveryToken: envía el correo al destinatario correcto")
    void sendRecoveryToken_sendsEmailToCorrectRecipient() {
        emailService.sendRecoveryToken("usuario@example.com", "abc12345");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertThat(sentMessage.getTo()).contains("usuario@example.com");
    }

    @Test
    @DisplayName("sendRecoveryToken: usa el asunto 'Password Recovery'")
    void sendRecoveryToken_hasCorrectSubject() {
        emailService.sendRecoveryToken("usuario@example.com", "abc12345");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getSubject()).isEqualTo("Password Recovery");
    }

    @Test
    @DisplayName("sendRecoveryToken: el cuerpo contiene el token")
    void sendRecoveryToken_bodyContainsToken() {
        emailService.sendRecoveryToken("usuario@example.com", "mi-token-secreto");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getText()).contains("mi-token-secreto");
    }

    @Test
    @DisplayName("sendRecoveryToken: el cuerpo menciona expiración en 15 minutos")
    void sendRecoveryToken_bodyMentionsExpiration() {
        emailService.sendRecoveryToken("usuario@example.com", "token123");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getText()).contains("15 minutos");
    }

    @Test
    @DisplayName("sendRecoveryToken: llama a mailSender exactamente una vez")
    void sendRecoveryToken_callsMailSenderOnce() {
        emailService.sendRecoveryToken("a@b.com", "token");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
