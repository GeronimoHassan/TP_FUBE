package com.tiendaonline.clientes.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.tiendaonline.clientes.dto.ClienteResponse;
import com.tiendaonline.clientes.service.ClienteService;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.Mockito.verify;

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
}