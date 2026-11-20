package com.tiendaonline.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Schema(description = "Formato de error de la API")
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje
) {
    public static ErrorResponse of(HttpStatus status, String mensaje) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensaje);
    }
}