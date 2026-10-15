# Prueba directa de correo — EV2

Esta ruta requiere desplegar la versión que la implementa. No basta agregarla en Gateway.

## API Gateway HTTP API de backend

- Método y ruta públicos: `POST /notificaciones/prueba-correo`.
- Integración HTTP: `http://<IP_ACTUAL_NOTIFICACIONES>:8085/api/notificaciones/prueba-correo`.
- Authorizer: JWT de Azure ya existente, `azure-jwt-authorizer`.
- Pasar `Authorization` al backend; mantener la validación JWT de Spring.
- Confirmar conectividad del Gateway al puerto 8085 y desplegar el stage si no tiene AutoDeploy.
- No se necesitan cambios en las rutas del carrito: ya existían.

## Postman

1. Método POST, URL `https://1nqf3okm71.execute-api.us-east-1.amazonaws.com/notificaciones/prueba-correo`.
2. Authorization → Bearer Token: access token vigente de Azure para la API.
   Mantenerlo en una variable local privada, nunca en Git, capturas ni chat.
3. Body → none. No se envía destinatario, pedido ni importe.
4. Send. El destinatario se obtiene del JWT validado: `email`,
   `preferred_username` o `upn` (primer correo válido).
5. Esperado: HTTP 200, `status: ENVIADO`. Significa aceptación por SMTP,
   no garantiza recepción final: comprobar bandeja/spam.

Errores: 401 sin JWT válido; 400 sin email válido en la cuenta; 502 si SMTP falla.
No se exponen errores internos del proveedor. La prueba es síncrona y no
publica mensajes RabbitMQ, no crea compras y no modifica stock ni reportes.
Cada petición exitosa envía un correo; no repetir Send innecesariamente.

## Pruebas locales

`./mvnw -Dtest=NotificacionesControllerTest,EmailServiceTest,RabbitMQConfigTest test`

Son pruebas unitarias sin SMTP real; no certifican despliegue ni envío remoto.
La autenticación HTTP queda a cargo del SecurityFilterChain existente;
los tests del controlador invocan sus métodos con un Jwt de prueba.
