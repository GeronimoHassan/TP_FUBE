package com.tiendaonline.clientes.service;

import com.tiendaonline.clientes.client.PedidosClient;
import com.tiendaonline.clientes.dto.ClienteCrearRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import jakarta.persistence.EntityManager;

import com.tiendaonline.clientes.dto.ClienteResponse;
import com.tiendaonline.clientes.dto.PedidoResponse;
import com.tiendaonline.clientes.entity.Cliente;
import com.tiendaonline.clientes.repository.ClienteRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {
    
    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private FeatureFlagService featureFlagService;

    @Mock
    private PedidosClient pedidosClient;

    @InjectMocks
    private ClienteService clienteService;


    @Test
    void buscarPorIdInexistenteLanzaNotFound() {
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteService.buscarPorId(99);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(featureFlagService, never()).debeEnmascarar(any());
    }

    @Test
    void buscarPorIdExistenteDevuelveSusDatos() {
        Cliente cliente = new Cliente("Ana Gómez", "ana@mail.com");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        ClienteResponse respuesta = clienteService.buscarPorId(1);

        assertEquals("Ana Gómez", respuesta.nombre());
        assertEquals("ana@mail.com", respuesta.email());
    }

    @Test
    void buscarPorIdConFlagActivadaEnmascaraDatos(){

        Cliente cliente = new Cliente("Ana Gómez","ana@gmail.com");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(featureFlagService.debeEnmascarar(1)).thenReturn(true);

        ClienteResponse respuesta = clienteService.buscarPorId(1);

        assertEquals("A*** G***", respuesta.nombre());
        assertEquals("****@****.com", respuesta.email());
    }

    @Test
    void registrarClienteNuevoDevuelveSusDatos() {
        ClienteCrearRequest request = new ClienteCrearRequest("Ana Gómez", "ana@gmail.com");
        Cliente cliente = new Cliente("Ana Gómez", "ana@gmail.com");

        when(clienteRepository.existsByEmail("ana@gmail.com")).thenReturn(false);
        when(clienteRepository.saveAndFlush(any())).thenReturn(cliente);

        ClienteResponse respuesta = clienteService.registrarCliente(request);

        assertEquals("Ana Gómez", respuesta.nombre());
        assertEquals("ana@gmail.com", respuesta.email());
        verify(entityManager).refresh(cliente);
    }

    @Test
    void registrarClienteConEmailDuplicadoLanzaConflict() {
        ClienteCrearRequest request = new ClienteCrearRequest("Ana Gómez", "ana@gmail.com");

        when(clienteRepository.existsByEmail("ana@gmail.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteService.registrarCliente(request);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(clienteRepository, never()).saveAndFlush(any());
    }

    @Test
    void buscarPorIdConFlagDesactivadaDevuelveDatosOriginales() {
        Cliente cliente = new Cliente("Ana Gómez", "ana@gmail.com");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(featureFlagService.debeEnmascarar(1)).thenReturn(false);

        ClienteResponse respuesta = clienteService.buscarPorId(1);

        assertEquals("Ana Gómez", respuesta.nombre());
        assertEquals("ana@gmail.com", respuesta.email());
    }

    @Test
    void listarOrdenesDeClienteInexistenteLanzaNotFound() {
        when(clienteRepository.existsById(99)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clienteService.listarOrdenesDeCliente(99);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(pedidosClient, never()).listarPorCliente(any());
    }

    @Test
    void listarOrdenesDeClienteExistenteDevuelveLosPedidos() {
        PedidoResponse pedido = new PedidoResponse(10, 1, "CREADO", new BigDecimal("1500.00"), null, null);

        when(clienteRepository.existsById(1)).thenReturn(true);
        when(pedidosClient.listarPorCliente(1)).thenReturn(List.of(pedido));

        List<PedidoResponse> pedidos = clienteService.listarOrdenesDeCliente(1);

        assertEquals(1, pedidos.size());
        assertEquals(10, pedidos.get(0).id());
    }

}
