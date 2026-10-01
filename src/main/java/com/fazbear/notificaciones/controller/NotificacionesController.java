package com.fazbear.notificaciones.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * NotificacionesController — expone un health check.
 * El MS no necesita endpoints REST propios ya que actúa como consumidor de mensajes.
 */
@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "https://w8xu8o4pd7.execute-api.us-east-1.amazonaws.com")
public class NotificacionesController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "ms-notificaciones"));
    }
}
