package com.fazbear.notificaciones.service;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {
    @Test
    void smtpFailureIsNotAcknowledgedAsSuccess() {
        JavaMailSender sender = mock(JavaMailSender.class);
        doThrow(new MailSendException("SMTP unavailable")).when(sender).send(any(SimpleMailMessage.class));
        EmailService service = new EmailService(sender);
        ReflectionTestUtils.setField(service, "fromEmail", "sender@example.com");
        assertThrows(AmqpRejectAndDontRequeueException.class,
            () -> service.enviarConfirmacion("user@example.com", 1L, new BigDecimal("12.99")));
    }

    @Test
    void confirmationIsSent() {
        JavaMailSender sender = mock(JavaMailSender.class);
        EmailService service = new EmailService(sender);
        ReflectionTestUtils.setField(service, "fromEmail", "sender@example.com");
        service.enviarConfirmacion("user@example.com", 1L, new BigDecimal("12.99"));
        verify(sender).send(any(SimpleMailMessage.class));
    }
}
