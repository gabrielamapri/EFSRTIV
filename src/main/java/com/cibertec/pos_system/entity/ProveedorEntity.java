package com.cibertec.pos_system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "proveedores")
@Data
public class ProveedorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     private Long id;
    private String ruc;
    private String razonSocial;
    private String direccion;
    private String telefono;
    private String email;
    private String contacto;
    private boolean activo;
}
