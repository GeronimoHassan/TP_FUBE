package com.tiendaonline.clientes.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import jakarta.persistence.EntityManager;
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

}
