package com.fazbear.notificaciones.config;

import com.fazbear.notificaciones.model.PedidoEvent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMQConfigTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void publisherTypeHeaderDoesNotPreventConversionToLocalEvent(boolean v2) {
        MessageProperties properties = new MessageProperties();
        properties.setContentType("application/json");
        properties.setHeader("__TypeId__", "com.fazbear.orden.model.PedidoEvent");
        properties.setInferredArgumentType(PedidoEvent.class);
        String extras = v2 ? ",\"schemaVersion\":2,\"eventoId\":\"9abe44c5-206a-4231-9ba0-14cd3101f42f\",\"items\":[{\"productoId\":1,\"cantidad\":2}]" : "";
        String json = """
            {"pedidoId":42,"usuarioId":"user","emailUsuario":"user@example.com",
             "total":12.99,"estado":"PENDIENTE","fechaCreacion":"2026-10-14T10:30:00"
            """ + extras + "}";
        Object result = new RabbitMQConfig().messageConverter().fromMessage(
            new Message(json.getBytes(StandardCharsets.UTF_8), properties));
        PedidoEvent event = assertInstanceOf(PedidoEvent.class, result);
        assertEquals(42L, event.getPedidoId());
        assertEquals("user@example.com", event.getEmailUsuario());
    }
}
