package com.tiendaonline.clientes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.tiendaonline.clientes.dto.ClienteResponse;
import com.tiendaonline.clientes.dto.PedidoResponse;
import com.tiendaonline.clientes.service.ClienteService;

import java.math.BigDecimal;
import java.util.List;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;        

    @MockBean
    private ClienteService clienteService; 

    @Test
    void getClienteExistenteDevuelve200() throws Exception {
        
        when(clienteService.buscarPorId(1))
                .thenReturn(new ClienteResponse(1,"Ana Gómez","ana@mail.com",null));

        
        mockMvc.perform(get("/clientes/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nombre").value("Ana Gómez"));
    }

    @Test
    void getClienteInexistenteDevuelve404() throws Exception {

        when(clienteService.buscarPorId(99))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/clientes/99"))
               .andExpect(status().isNotFound());
        
        verify(clienteService).buscarPorId(99);
    }

    @Test
    void postClienteValidoDevuelve201() throws Exception {

        when(clienteService.registrarCliente(any()))
                .thenReturn(new ClienteResponse(1, "Ana Gómez", "ana@mail.com", null));

        String json = """
                {
                  "nombre": "Ana Gómez",
                  "email": "ana@mail.com"
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.nombre").value("Ana Gómez"))
               .andExpect(jsonPath("$.email").value("ana@mail.com"));
    }

    @Test
    void postClienteSinNombreDevuelve400() throws Exception {

        String json = """
                {
                  "nombre": "",
                  "email": "ana@mail.com"
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
               .andExpect(status().isBadRequest());

        verify(clienteService, never()).registrarCliente(any());
    }

    @Test
    void postClienteConEmailInvalidoDevuelve400() throws Exception {

        String json = """
                {
                  "nombre": "Ana Gómez",
                  "email": "esto-no-es-un-email"
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
               .andExpect(status().isBadRequest());

        verify(clienteService, never()).registrarCliente(any());
    }

    @Test
    void postClienteConEmailDuplicadoDevuelve409() throws Exception {

        when(clienteService.registrarCliente(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        String json = """
                {
                  "nombre": "Ana Gómez",
                  "email": "ana@mail.com"
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
               .andExpect(status().isConflict());
    }

    @Test
    void getOrdenesDeClienteExistenteDevuelve200() throws Exception {

        when(clienteService.listarOrdenesDeCliente(1))
                .thenReturn(List.of(new PedidoResponse(10, 1, "CREADO", new BigDecimal("1500.00"), null, null)));

        mockMvc.perform(get("/clientes/1/ordenes"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(10))
               .andExpect(jsonPath("$[0].estado").value("CREADO"));
    }

    @Test
    void getOrdenesDeClienteInexistenteDevuelve404() throws Exception {

        when(clienteService.listarOrdenesDeCliente(99))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/clientes/99/ordenes"))
               .andExpect(status().isNotFound());
    }
}