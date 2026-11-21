
package com.tiendaonline.clientes.client;

import com.tiendaonline.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpError;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PedidosClientTest {

    private static final ClientAndServer mockServer =
            ClientAndServer.startClientAndServer(0);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteRepository clienteRepository;

    @DynamicPropertySource
    static void configurarUrl(DynamicPropertyRegistry registry) {
        registry.add("servicios.pedidos.url",
                () -> "http://localhost:" + mockServer.getLocalPort());
    }

    @BeforeEach
    void limpiarServidor() {
        mockServer.reset();
    }

    @AfterAll
    static void cerrarServidor() {
        mockServer.stop();
    }

    @Test
    void clienteExistenteConPedidosDevuelveLista() throws Exception {

        when(clienteRepository.existsById(1)).thenReturn(true);

        String json = """
                [
                  {
                    "id": 10,
                    "clienteId": 1,
                    "estado": "CREADO",
                    "montoTotal": 1500.00,
                    "fechaCreacion": "2026-11-15T10:30:00",
                    "fechaActualizacion": "2026-11-15T10:30:00"
                  }
                ]
                """;

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos")
                        .withQueryStringParameter("id_cliente", "1")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)
        );

        mockMvc.perform(get("/clientes/1/ordenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].clienteId").value(1))
                .andExpect(jsonPath("$[0].estado").value("CREADO"));

        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos")
                        .withQueryStringParameter("id_cliente", "1"),
                VerificationTimes.once()
        );
    }

    @Test
    void clienteSinPedidosDevuelveListaVacia() throws Exception {

        when(clienteRepository.existsById(1)).thenReturn(true);

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos")
                        .withQueryStringParameter("id_cliente", "1")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")
        );

        mockMvc.perform(get("/clientes/1/ordenes"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void clienteInexistenteDevuelve404() throws Exception {

        when(clienteRepository.existsById(999)).thenReturn(false);

        mockMvc.perform(get("/clientes/999/ordenes"))
                .andExpect(status().isNotFound());

        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos"),
                VerificationTimes.exactly(0)
        );
    }

    @Test
    void pedidosConError500Devuelve502() throws Exception {

        when(clienteRepository.existsById(1)).thenReturn(true);

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos")
                        .withQueryStringParameter("id_cliente", "1")
        ).respond(
                response().withStatusCode(500)
        );

        mockMvc.perform(get("/clientes/1/ordenes"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void pedidosConError400Devuelve502() throws Exception {

        when(clienteRepository.existsById(1)).thenReturn(true);

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos")
                        .withQueryStringParameter("id_cliente", "1")
        ).respond(
                response().withStatusCode(400)
        );

        mockMvc.perform(get("/clientes/1/ordenes"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void pedidosNoDisponibleDevuelve503() throws Exception {

        when(clienteRepository.existsById(1)).thenReturn(true);

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos")
                        .withQueryStringParameter("id_cliente", "1")
        ).error(
                HttpError.error().withDropConnection(true)
        );

        mockMvc.perform(get("/clientes/1/ordenes"))
                .andExpect(status().isServiceUnavailable());
    }


    @Test
    void pedidosConTimeoutDevuelve504() throws Exception {

        //existe cliente
        when(clienteRepository.existsById(1)).thenReturn(true);

        //el pedido tarda 6 seg en responder
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos")
                        .withQueryStringParameter("id_cliente", "1")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")
                        .withDelay(TimeUnit.SECONDS, 6)
        );

        //chequeamos q cliente devuelve 504
        mockMvc.perform(get("/clientes/1/ordenes"))
                .andExpect(status().isGatewayTimeout());

        //vemos si se realizo la solicitud
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/pedidos")
                        .withQueryStringParameter("id_cliente", "1"),
                VerificationTimes.once()
        );
    }

}
