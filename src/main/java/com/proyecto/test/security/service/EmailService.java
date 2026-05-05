package com.proyecto.test.security.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarTokenRecuperacion(String correoDestino, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correoDestino);
        mensaje.setSubject("Recuperación de contraseña");
        mensaje.setText(
            "Hola,\n\n" +
            "Recibimos una solicitud para recuperar tu contraseña.\n\n" +
            "Tu contraseña de recuperación es:\n\n" +
            token + "\n\n" +
            "Esta contraseña expira en 15 minutos.\n\n" +
            "Si no solicitaste la recuperación de tu contraseña, ignora este correo.\n\n" +
            "Saludos,\nEl equipo de soporte"
        );

        mailSender.send(mensaje);
    }
}
