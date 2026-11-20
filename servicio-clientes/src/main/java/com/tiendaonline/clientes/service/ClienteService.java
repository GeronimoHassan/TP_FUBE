package com.tiendaonline.clientes.service;

import com.statsig.sdk.Statsig;
import com.statsig.sdk.StatsigUser;
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
    private final FeatureFlagService featureFlagService;

    public ClienteService(ClienteRepository clienteRepository,
                          EntityManager entityManager, FeatureFlagService featureFlagService) {
        this.clienteRepository = clienteRepository;
        this.entityManager = entityManager;
        this.featureFlagService = featureFlagService;
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
        return aResponse(clienteGuardado);
    }

    public ClienteResponse buscarPorId(Integer id) {

        //Comprobamos si el cliente existe
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado"));


        boolean ocultarDatos = featureFlagService.debeEnmascarar(id);

        if (!ocultarDatos) {
        // Devolvemos los datos del cliente
        return aResponse(cliente);
        }

        return new ClienteResponse(cliente.getId(), enmascararNombre(cliente.getNombre()), enmascararEmail(cliente.getEmail()), cliente.getFechaAlta());

    }


    private String enmascararNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "****";
        }

        String[] palabras = nombre.trim().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (resultado.length() > 0) {
                resultado.append(" ");
            }

            resultado.append(palabra.charAt(0)).append("***");
        }

        return resultado.toString();
    }


    private String enmascararEmail(String email) {
        if (email == null || email.isBlank()) {
            return "****";
        }

        int arroba = email.indexOf('@');
        int ultimoPunto = email.lastIndexOf('.');

        if (arroba <= 0 || ultimoPunto <= arroba + 1) {
            return "****";
        }

        String extension = email.substring(ultimoPunto);

        return "****@****" + extension;
    }



    private ClienteResponse aResponse(Cliente cliente) {
        return new ClienteResponse(
            cliente.getId(),
            cliente.getNombre(),
            cliente.getEmail(),
            cliente.getFechaAlta()
        );
    }
}