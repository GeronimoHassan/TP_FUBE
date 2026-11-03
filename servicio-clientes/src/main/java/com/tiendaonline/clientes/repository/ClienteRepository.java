package com.tiendaonline.clientes.repository;

import com.tiendaonline.clientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    boolean existsByEmail(String email);

}