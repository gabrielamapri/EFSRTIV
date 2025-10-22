package com.cibertec.pos_system.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "locales")
@Data
public class LocalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String horario;
    private boolean activo;
}
