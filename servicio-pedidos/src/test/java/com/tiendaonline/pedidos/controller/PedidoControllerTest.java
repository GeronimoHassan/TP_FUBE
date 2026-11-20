package com.tiendaonline.pedidos.controller;

import com.tiendaonline.pedidos.dto.ItemPedidoResponse;
import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.entity.EstadoPedido;
import com.tiendaonline.pedidos.exception.PedidoNoEncontradoException;
import com.tiendaonline.pedidos.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    private PedidoResponse respuesta(Integer id, Integer clienteId) {
        return new PedidoResponse(id, clienteId, EstadoPedido.CREADO, new BigDecimal("21.00"),
                LocalDateTime.of(2026, 11, 1, 10, 0), LocalDateTime.of(2026, 11, 1, 10, 0),
                List.of(new ItemPedidoResponse(101, 2, new BigDecimal("10.50"), new BigDecimal("21.00"))));
    }

    @Test
    void getPedidoPorIdDevuelve200ConElPedido() throws Exception {
        when(pedidoService.obtenerPorId(7)).thenReturn(respuesta(7, 5));

        mockMvc.perform(get("/pedidos/7")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(7)).andExpect(jsonPath("$.clienteId").value(5)).andExpect(jsonPath("$.estado").value("CREADO"))
                .andExpect(jsonPath("$.montoTotal").value(21.00))
                .andExpect(jsonPath("$.items[0].productoId").value(101));
    }

    @Test
    void getPedidoInexistenteDevuelve404() throws Exception {
        when(pedidoService.obtenerPorId(99)).thenThrow(new PedidoNoEncontradoException(99));

        mockMvc.perform(get("/pedidos/99")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.mensaje").value("No existe un pedido con id 99"));
    }

    @Test
    void getPedidoConIdNoNumericoDevuelve400() throws Exception {
        mockMvc.perform(get("/pedidos/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void listarPorClienteDevuelve200ConLaLista() throws Exception {
        when(pedidoService.listarPorCliente(5)).thenReturn(List.of(respuesta(8, 5), respuesta(7, 5)));

        mockMvc.perform(get("/pedidos").param("id_cliente", "5")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].id").value(8));
    }

    @Test
    void listarPorClienteSinPedidosDevuelve200ConListaVacia() throws Exception {
        when(pedidoService.listarPorCliente(42)).thenReturn(List.of());

        mockMvc.perform(get("/pedidos").param("id_cliente", "42")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listarSinIdClienteDevuelve400() throws Exception {
        mockMvc.perform(get("/pedidos")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensaje").value("Falta el parámetro obligatorio 'id_cliente'"));
    }
}