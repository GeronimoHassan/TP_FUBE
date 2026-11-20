package com.tiendaonline.pedidos.service;

import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.entity.EstadoPedido;
import com.tiendaonline.pedidos.entity.ItemPedido;
import com.tiendaonline.pedidos.entity.Pedido;
import com.tiendaonline.pedidos.exception.PedidoNoEncontradoException;
import com.tiendaonline.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedido(Integer id, Integer clienteId) {
        Pedido pedido = new Pedido(clienteId);
        pedido.setId(id);
        pedido.setFechaCreacion(LocalDateTime.of(2026, 11, 1, 10, 0));
        pedido.setFechaActualizacion(LocalDateTime.of(2026, 11, 1, 10, 0));
        pedido.agregarItem(new ItemPedido(101, 2, new BigDecimal("10.50")));
        pedido.agregarItem(new ItemPedido(102, 3, new BigDecimal("0.10")));
        pedido.recalcularMontoTotal();
        return pedido;
    }

    @Test
    void obtenerPorIdDevuelvePedidoConItemsYMonto() {
        when(pedidoRepository.findByIdConItems(7)).thenReturn(Optional.of(pedido(7, 5)));

        PedidoResponse respuesta = pedidoService.obtenerPorId(7);

        assertThat(respuesta.id()).isEqualTo(7);
        assertThat(respuesta.clienteId()).isEqualTo(5);
        assertThat(respuesta.estado()).isEqualTo(EstadoPedido.CREADO);
        assertThat(respuesta.montoTotal()).isEqualByComparingTo("21.30");
        assertThat(respuesta.items()).hasSize(2);
        assertThat(respuesta.items().get(0).subtotal()).isEqualByComparingTo("21.00");
    }

    @Test
    void obtenerPorIdInexistenteLanzaPedidoNoEncontrado() {
        when(pedidoRepository.findByIdConItems(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.obtenerPorId(99))
                .isInstanceOf(PedidoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void listarPorClienteDevuelveSusPedidos() {
        when(pedidoRepository.findByClienteIdOrderByFechaCreacionDesc(5))
                .thenReturn(List.of(pedido(8, 5), pedido(7, 5)));

        List<PedidoResponse> respuesta = pedidoService.listarPorCliente(5);

        assertThat(respuesta).extracting(PedidoResponse::id).containsExactly(8, 7);
    }

    @Test
    void listarPorClienteSinPedidosDevuelveListaVacia() {
        when(pedidoRepository.findByClienteIdOrderByFechaCreacionDesc(42)).thenReturn(List.of());

        assertThat(pedidoService.listarPorCliente(42)).isEmpty();
    }
}