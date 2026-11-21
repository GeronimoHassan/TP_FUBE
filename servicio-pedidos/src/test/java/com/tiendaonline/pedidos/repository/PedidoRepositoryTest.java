package com.tiendaonline.pedidos.repository;

import com.tiendaonline.pedidos.entity.EstadoPedido;
import com.tiendaonline.pedidos.entity.ItemPedido;
import com.tiendaonline.pedidos.entity.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private TestEntityManager em;

    private Pedido pedidoConDosItems(Integer clienteId) {
        Pedido pedido = new Pedido(clienteId);
        pedido.agregarItem(new ItemPedido(101, 2, new BigDecimal("10.50")));
        pedido.agregarItem(new ItemPedido(102, 3, new BigDecimal("0.10")));
        pedido.recalcularMontoTotal();
        return pedido;
    }

    @Test
    void guardaPedidoConItemsYCalculaMontoConBigDecimal() {
        Pedido guardado = pedidoRepository.saveAndFlush(pedidoConDosItems(2002));
        em.clear();

        Pedido leido = pedidoRepository.findByIdConItems(guardado.getId()).orElseThrow();

        assertThat(leido.getEstado()).isEqualTo(EstadoPedido.CREADO);
        assertThat(leido.getItems()).hasSize(2);
        // 2 * 10.50 + 3 * 0.10 = 21.30 (sin errores de punto flotante)
        assertThat(leido.getMontoTotal()).isEqualByComparingTo("21.30");
        assertThat(leido.getFechaCreacion()).isNotNull();
        assertThat(leido.getFechaActualizacion()).isNotNull();
    }

    @Test
    void listaPedidosDeUnClienteYDevuelveVacioSiNoTiene() {
        Integer clienteId = 1001;
        pedidoRepository.save(pedidoConDosItems(clienteId));
        pedidoRepository.save(pedidoConDosItems(clienteId));
        pedidoRepository.save(pedidoConDosItems(2002));
        em.flush();

        assertThat(pedidoRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId)).hasSize(2);
        assertThat(pedidoRepository.findByClienteIdOrderByFechaCreacionDesc(999999)).isEmpty();
    }

    @Test
    void findByIdConItemsDevuelveVacioSiNoExiste() {
        assertThat(pedidoRepository.findByIdConItems(999999)).isEmpty();
    }

    @Test
    void buscaCandidatosAExpirarSoloCreadosAnterioresAlLimite() {
        Pedido viejo = pedidoRepository.saveAndFlush(pedidoConDosItems(2002));
        Pedido reciente = pedidoRepository.saveAndFlush(pedidoConDosItems(2002));
        Pedido viejoCancelado = pedidoConDosItems(2002);
        viejoCancelado.setEstado(EstadoPedido.CANCELADO);
        viejoCancelado = pedidoRepository.saveAndFlush(viejoCancelado);

        em.getEntityManager()
                .createNativeQuery("update pedidos set fecha_creacion = :f where id in (:a, :b)")
                .setParameter("f", LocalDateTime.now().minusHours(25))
                .setParameter("a", viejo.getId())
                .setParameter("b", viejoCancelado.getId())
                .executeUpdate();
        em.clear();

        List<Pedido> candidatos = pedidoRepository
                .findByEstadoAndFechaCreacionBeforeOrderByFechaCreacionAscIdAsc(
                        EstadoPedido.CREADO, LocalDateTime.now().minusHours(24), PageRequest.of(0, 10))
                .getContent();

        assertThat(candidatos).extracting(Pedido::getId)
                .contains(viejo.getId())
                .doesNotContain(reciente.getId(), viejoCancelado.getId());
    }

    @Test
    void estadoCancelableSoloDesdeCreadoOConfirmado() {
        assertThat(EstadoPedido.CREADO.puedeCancelarse()).isTrue();
        assertThat(EstadoPedido.CONFIRMADO.puedeCancelarse()).isTrue();
        assertThat(EstadoPedido.CANCELADO.puedeCancelarse()).isFalse();
        assertThat(EstadoPedido.EXPIRADO.puedeCancelarse()).isFalse();
    }
}