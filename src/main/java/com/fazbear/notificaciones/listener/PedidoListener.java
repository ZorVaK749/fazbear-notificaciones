package com.fazbear.notificaciones.listener;

import com.fazbear.notificaciones.model.PedidoEvent;
import com.fazbear.notificaciones.config.RabbitMQConfig;
import com.fazbear.notificaciones.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * PedidoListener — escucha la cola pedido.creado.queue de RabbitMQ.
 * Cuando ms-orden publica un nuevo pedido, este listener lo recibe
 * y dispara el envío del correo de confirmación al usuario.
 */
@Component
public class PedidoListener {

    private static final Logger log = LoggerFactory.getLogger(PedidoListener.class);

    private final EmailService emailService;

    public PedidoListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void onPedidoCreado(PedidoEvent evento) {
        log.info("Mensaje recibido de RabbitMQ: pedidoId={}, email={}",
            evento.getPedidoId(), evento.getEmailUsuario());
        emailService.enviarConfirmacion(
            evento.getEmailUsuario(),
            evento.getPedidoId(),
            evento.getTotal()
        );
    }
}
