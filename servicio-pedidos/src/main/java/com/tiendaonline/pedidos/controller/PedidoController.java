package com.tiendaonline.pedidos.controller;

import com.tiendaonline.pedidos.dto.ErrorResponse;
import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping("/{id}")
    @Operation(summary = "Consulta un pedido por su id")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado")
    @ApiResponse(responseCode = "404", description = "El pedido no existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public PedidoResponse obtener(@PathVariable Integer id) {
        return pedidoService.obtenerPorId(id);
    }

    @GetMapping
    @Operation(summary = "Lista los pedidos de un cliente", description = "Devuelve una lista vacía si el cliente no tiene pedidos.")
    @ApiResponse(responseCode = "200", description = "Pedidos del cliente")
    @ApiResponse(responseCode = "400", description = "Falta id_cliente o no es un número", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<PedidoResponse> listarPorCliente(@RequestParam("id_cliente") Integer idCliente) {
        return pedidoService.listarPorCliente(idCliente);
    }
}