package com.tiendaonline.pedidos.repository;

import com.tiendaonline.pedidos.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer> {

    List<ItemPedido> findByPedidoId(Integer pedidoId);
}