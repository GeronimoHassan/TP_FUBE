
package com.tiendaonline.clientes.config;

import com.statsig.sdk.Statsig;
import com.statsig.sdk.StatsigOptions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.util.concurrent.ExecutionException;

@Configuration
@Profile("!test")
public class StatsigConfig {

    @PostConstruct
    public void inicializar() {

        String secreto = System.getenv("STATSIG_SERVER_SECRET");

        if (secreto == null || secreto.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar STATSIG_SERVER_SECRET"
            );
        }

        try {
            StatsigOptions options = new StatsigOptions();
            options.setInitTimeoutMs(15000);

            Statsig.initializeAsync(secreto, options).get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Se interrumpió la inicialización de Statsig", e
            );

        } catch (ExecutionException e) {
            throw new IllegalStateException(
                    "No se pudo inicializar Statsig", e
            );
        }
    }

    @PreDestroy
    public void cerrar() {
        Statsig.shutdown();
    }
}
