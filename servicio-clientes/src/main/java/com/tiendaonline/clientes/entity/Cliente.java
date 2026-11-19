package com.tiendaonline.clientes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


     //lombok no se q es pero me lo recomendo intelij desp de ahcer los getters y setter
    @Getter
    @Setter
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

     @Setter
     @Getter
     @Column(name = "email", nullable = false, unique = true,length = 255)
    private String email;

    @Getter
    @Column(name = "fecha_alta", insertable = false, updatable = false)
    private LocalDateTime fechaAlta;

    public Cliente(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    protected Cliente() {
    }

    public Integer getId() {
        return id;
    }

}
