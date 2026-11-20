package com.tiendaonline.pedidos.service;

import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.exception.PedidoNoEncontradoException;
import com.tiendaonline.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorId(Integer id) {
        return pedidoRepository.findByIdConItems(id)
                .map(PedidoResponse::from)
                .orElseThrow(() -> new PedidoNoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorCliente(Integer clienteId) {
        return pedidoRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId).stream()
                .map(PedidoResponse::from)
                .toList();
    }
}