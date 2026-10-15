package com.fazbear.notificaciones.controller;

import com.fazbear.notificaciones.service.EmailService;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Health check y prueba SMTP autenticada, independiente del consumidor RabbitMQ.
 */
@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "https://w8xu8o4pd7.execute-api.us-east-1.amazonaws.com")
public class NotificacionesController {
    private final EmailService emailService;

    public NotificacionesController(EmailService emailService) {
        this.emailService = emailService;
    }

    /** Sin destinatario libre: solo envía a la identidad del JWT validado. */
    @PostMapping("/prueba-correo")
    public ResponseEntity<Map<String, String>> pruebaCorreo(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String destinatario = correoCuenta(jwt);
        try {
            emailService.enviarPrueba(destinatario);
        } catch (MailException error) {
            // Nunca exponer detalles SMTP, credenciales ni cuerpos del proveedor.
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "SMTP no confirmó el envío. Verifique la configuración del servicio.");
        }
        return ResponseEntity.ok(Map.of("status", "ENVIADO",
            "mensaje", "Correo de prueba aceptado por SMTP; revise su bandeja y spam."));
    }

    private String correoCuenta(Jwt jwt) {
        for (String claim : new String[]{"email", "preferred_username", "upn"}) {
            Object valor = jwt.getClaims().get(claim);
            if (!(valor instanceof String correo) || correo.length() > 254
                    || correo.contains("\r") || correo.contains("\n")) continue;
            try {
                InternetAddress direccion = new InternetAddress(correo, true);
                direccion.validate();
                if (direccion.getPersonal() == null && correo.equals(direccion.getAddress())
                        && correo.contains("@")) return correo;
            } catch (AddressException ignored) {
                // Algunos tokens no incluyen un email utilizable: probar el siguiente claim.
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "La cuenta autenticada no contiene un correo válido.");
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "ms-notificaciones"));
    }
}
