package com.tiendaonline.pedidos.repository;

import com.tiendaonline.pedidos.entity.EstadoPedido;
import com.tiendaonline.pedidos.entity.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    @EntityGraph(attributePaths = "items")
    List<Pedido> findByClienteIdOrderByFechaCreacionDesc(Integer clienteId);

    @Query("select p from Pedido p left join fetch p.items where p.id = :id")
    Optional<Pedido> findByIdConItems(@Param("id") Integer id);

    Page<Pedido> findByEstadoAndFechaCreacionBeforeOrderByFechaCreacionAscIdAsc(
            EstadoPedido estado, LocalDateTime limite, Pageable pageable);
}