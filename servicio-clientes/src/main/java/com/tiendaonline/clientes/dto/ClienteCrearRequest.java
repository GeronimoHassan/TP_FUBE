package com.tiendaonline.clientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteCrearRequest(

        @NotBlank
        @Size(max = 255)
        String nombre,

        @NotBlank
        @Email
        @Size(max = 255)
        String email

) {}