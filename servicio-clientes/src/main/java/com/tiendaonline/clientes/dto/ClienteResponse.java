package com.tiendaonline.clientes.dto;

import java.time.LocalDateTime;

public record ClienteResponse(

        Integer id,
        String nombre,
        String email,
        LocalDateTime fechaAlta

) {}