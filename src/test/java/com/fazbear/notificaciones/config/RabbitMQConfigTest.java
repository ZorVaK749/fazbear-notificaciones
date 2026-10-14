package com.fazbear.notificaciones.config;

import com.fazbear.notificaciones.model.PedidoEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMQConfigTest {
    @Test
    void publisherTypeHeaderDoesNotPreventConversionToLocalEvent() {
        MessageProperties properties = new MessageProperties();
        properties.setContentType("application/json");
        properties.setHeader("__TypeId__", "com.fazbear.orden.model.PedidoEvent");
        properties.setInferredArgumentType(PedidoEvent.class);
        String json = """
            {"pedidoId":42,"usuarioId":"user","emailUsuario":"user@example.com",
             "total":12.99,"estado":"PENDIENTE","fechaCreacion":"2026-10-14T10:30:00"}
            """;
        Object result = new RabbitMQConfig().messageConverter().fromMessage(
            new Message(json.getBytes(StandardCharsets.UTF_8), properties));
        PedidoEvent event = assertInstanceOf(PedidoEvent.class, result);
        assertEquals(42L, event.getPedidoId());
        assertEquals("user@example.com", event.getEmailUsuario());
    }
}
