package com.tiendaonline.clientes.service;

import com.tiendaonline.clientes.dto.ClienteCrearRequest;
import com.tiendaonline.clientes.dto.ClienteResponse;
import com.tiendaonline.clientes.entity.Cliente;
import com.tiendaonline.clientes.repository.ClienteRepository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EntityManager entityManager;

    public ClienteService(ClienteRepository clienteRepository,
                          EntityManager entityManager) {
        this.clienteRepository = clienteRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public ClienteResponse registrarCliente(ClienteCrearRequest request) {

        // Comprobamos si el email ya esta registrado
        if (clienteRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El email ya esta registrado"
            );
        }

        // Creamos el cliente
        Cliente cliente = new Cliente(
                request.nombre(),
                request.email()
        );

        // Guardamos en la base de datos
        Cliente clienteGuardado = clienteRepository.saveAndFlush(cliente);

        // Recuperamos la fecha generada por PostgreSQL
        entityManager.refresh(clienteGuardado);

        // Devolvemos los datos del cliente
        return new ClienteResponse(
                clienteGuardado.getId(),
                clienteGuardado.getNombre(),
                clienteGuardado.getEmail(),
                clienteGuardado.getFechaAlta()
        );
    }

    public ClienteResponse buscarPorId(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getFechaAlta()
        );
    }
}