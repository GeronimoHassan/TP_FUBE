package com.tiendaonline.pedidos.dto;

import com.tiendaonline.pedidos.entity.EstadoPedido;
import com.tiendaonline.pedidos.entity.Pedido;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Pedido con sus ítems")
public record PedidoResponse(
        Integer id,
        Integer clienteId,
        EstadoPedido estado,
        BigDecimal montoTotal,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        List<ItemPedidoResponse> items
) {
    public static PedidoResponse from(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getEstado(),
                pedido.getMontoTotal(),
                pedido.getFechaCreacion(),
                pedido.getFechaActualizacion(),
                pedido.getItems().stream().map(ItemPedidoResponse::from).toList());
    }
}