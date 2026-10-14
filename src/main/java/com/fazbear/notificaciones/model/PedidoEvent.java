package com.fazbear.notificaciones.model;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Evento recibido desde RabbitMQ (publicado por ms-orden). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoEvent {

    private Long pedidoId;
    private String usuarioId;
    private String emailUsuario;
    private BigDecimal total;
    private String estado;
    private String fechaCreacion;

    public PedidoEvent() {}

    public Long getPedidoId()              { return pedidoId; }
    public void setPedidoId(Long v)        { this.pedidoId = v; }

    public String getUsuarioId()           { return usuarioId; }
    public void setUsuarioId(String v)     { this.usuarioId = v; }

    public String getEmailUsuario()        { return emailUsuario; }
    public void setEmailUsuario(String v)  { this.emailUsuario = v; }

    public BigDecimal getTotal()           { return total; }
    public void setTotal(BigDecimal v)     { this.total = v; }

    public String getEstado()              { return estado; }
    public void setEstado(String v)        { this.estado = v; }

    public String getFechaCreacion()        { return fechaCreacion; }
    public void setFechaCreacion(String v)  { this.fechaCreacion = v; }
}
