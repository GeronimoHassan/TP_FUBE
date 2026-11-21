
package com.tiendaonline.clientes.client;

import com.tiendaonline.clientes.dto.PedidoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.SocketTimeoutException;
import java.util.List;

@Component
public class PedidosClient {

    private final RestClient restClient;



    public PedidosClient(RestClient.Builder builder, @Value("${servicios.pedidos.url}") String url) {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(2000);
        factory.setReadTimeout(5000);

        this.restClient = builder.baseUrl(url).requestFactory(factory).build();
    }

    public List<PedidoResponse> listarPorCliente(Integer clienteId) {

        try {
            List<PedidoResponse> pedidos = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/pedidos")
                            .queryParam("id_cliente", clienteId)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<PedidoResponse>>() {});

            if (pedidos == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "El servicio de pedidos devolvió una respuesta vacía"
                );
            }

            return pedidos;

        } catch (ResourceAccessException e) {

            Throwable causa = e;

            while (causa != null) {
                if (causa instanceof SocketTimeoutException) {
                    throw new ResponseStatusException(
                            HttpStatus.GATEWAY_TIMEOUT,
                            "Tiempo de espera agotado al consultar pedidos",
                            e
                    );
                }

                causa = causa.getCause();
            }

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio de pedidos no está disponible",
                    e
            );

        } catch (RestClientResponseException e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "El servicio de pedidos respondió con un error",
                    e
            );

        } catch (RestClientException e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo procesar la respuesta de pedidos",
                    e
            );
        }
    }


}
