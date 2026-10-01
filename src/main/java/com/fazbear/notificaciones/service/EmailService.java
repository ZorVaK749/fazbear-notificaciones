package com.fazbear.notificaciones.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía correo de confirmación de pedido al cliente.
     *
     * @param destinatario email del usuario extraído del JWT
     * @param pedidoId     ID del pedido creado
     * @param total        monto total de la orden
     */
    public void enviarConfirmacion(String destinatario, Long pedidoId, BigDecimal total) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(fromEmail);
            mensaje.setTo(destinatario);
            mensaje.setSubject("🍕 ¡Tu pedido en Freddy Fazbear's Pizza fue confirmado! #" + pedidoId);
            mensaje.setText(buildEmailBody(pedidoId, total));
            mailSender.send(mensaje);
            log.info("Correo de confirmación enviado a {} para pedidoId={}", destinatario, pedidoId);
        } catch (Exception e) {
            log.error("Error al enviar correo a {}: {}", destinatario, e.getMessage());
        }
    }

    private String buildEmailBody(Long pedidoId, BigDecimal total) {
        return """
                ¡Hola, amigo de Freddy Fazbear's Pizza!

                Tu pedido ha sido CONFIRMADO exitosamente.

                Detalles:
                  - Número de Pedido: #%d
                  - Total:            $%.2f
                  - Estado:           PENDIENTE

                Nuestros animatrónicos ya están preparando tu orden.
                En breve recibirás una actualización cuando esté listo.

                ¡Que tengas una noche increíble!
                — El equipo de Freddy Fazbear's Pizza 🐻🍕
                """.formatted(pedidoId, total);
    }
}
