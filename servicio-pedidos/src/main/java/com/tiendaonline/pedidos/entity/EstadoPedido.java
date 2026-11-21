package com.tiendaonline.pedidos.entity;

public enum EstadoPedido {
    CREADO,
    CONFIRMADO,
    CANCELADO,
    EXPIRADO;

    public boolean puedeCancelarse() {
        return this == CREADO || this == CONFIRMADO;
    }
}