package com.tiendaonline.pedidos.exception;

public class PedidoNoEncontradoException extends RuntimeException {

    public PedidoNoEncontradoException(Integer id) {
        super("No existe un pedido con id " + id);
    }
}