package com.tiendaonline.clientes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoResponse(

        Integer id,
        Integer clienteId,
        String estado,
        BigDecimal montoTotal,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion


) { }
