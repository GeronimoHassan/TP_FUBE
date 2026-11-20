package com.tiendaonline.pedidos.dto;

import com.tiendaonline.pedidos.entity.ItemPedido;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Ítem de un pedido")
public record ItemPedidoResponse(
        Integer productoId,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
    public static ItemPedidoResponse from(ItemPedido item) {
        return new ItemPedidoResponse(
                item.getProductoId(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.subtotal());
    }
}