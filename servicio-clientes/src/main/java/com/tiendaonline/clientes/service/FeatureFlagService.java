
package com.tiendaonline.clientes.service;

import com.statsig.sdk.Statsig;
import com.statsig.sdk.StatsigUser;
import org.springframework.stereotype.Service;

@Service
public class FeatureFlagService {

    public boolean debeEnmascarar(Integer clienteId) {

        StatsigUser usuario = new StatsigUser("cliente-" + clienteId);

        return Statsig.checkGateSync(usuario, "mask-pii");
    }
}
