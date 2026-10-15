package com.fazbear.notificaciones.controller;

import com.fazbear.notificaciones.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificacionesControllerTest {
    private final EmailService email = mock(EmailService.class);
    private final NotificacionesController controller = new NotificacionesController(email);

    private Jwt token(String claim, String valor) {
        return Jwt.withTokenValue("test").header("alg", "RS256")
            .claim("sub", "usuario-1").claim(claim, valor).build();
    }

    @Test
    void enviaSoloACuentaAutenticada() {
        var respuesta = controller.pruebaCorreo(token("preferred_username", "user@example.com"));
        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals("ENVIADO", respuesta.getBody().get("status"));
        verify(email).enviarPrueba("user@example.com");
    }

    @Test
    void aceptaClaimEmail() {
        controller.pruebaCorreo(token("email", "user@example.com"));
        verify(email).enviarPrueba("user@example.com");
    }

    @Test
    void rechazaSinSesion() {
        var error = assertThrows(ResponseStatusException.class, () -> controller.pruebaCorreo(null));
        assertEquals(401, error.getStatusCode().value());
        verifyNoInteractions(email);
    }

    @Test
    void rechazaCorreoInvalidoMultipleOConCabeceras() {
        for (String valor : new String[]{"sin-correo", "a@example.com,b@example.com",
                "a@example.com\r\nBcc: b@example.com", "Nombre <a@example.com>"}) {
            var error = assertThrows(ResponseStatusException.class,
                () -> controller.pruebaCorreo(token("email", valor)));
            assertEquals(400, error.getStatusCode().value());
        }
        verifyNoInteractions(email);
    }

    @Test
    void falloSmtpNoSeDeclaraExitoNiExponeDetalles() {
        doThrow(new MailSendException("detalle privado SMTP"))
            .when(email).enviarPrueba("user@example.com");
        var error = assertThrows(ResponseStatusException.class,
            () -> controller.pruebaCorreo(token("email", "user@example.com")));
        assertEquals(502, error.getStatusCode().value());
        assertFalse(error.getReason().contains("detalle privado"));
    }
}
