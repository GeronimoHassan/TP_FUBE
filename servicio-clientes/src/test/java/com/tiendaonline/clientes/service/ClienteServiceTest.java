package com.tiendaonline.clientes.service;

import com.tiendaonline.clientes.client.PedidosClient;
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
import com.tiendaonline.clientes.entity.Cliente;
import com.tiendaonline.clientes.repository.ClienteRepository;
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

}
