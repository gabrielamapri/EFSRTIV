package com.cibertec.pos_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "arqueos_caja")
@Data
public class ArqueoCajaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private CajaSesionEntity cajaSesion;

    private LocalDateTime fechaArqueo;

    private double montoSistema;   // Lo que el sistema espera
    private double montoFisico;    // Lo contado por el cajero
    private double diferencia;     // montoFisico - montoSistema

    private String observaciones;
    private String usuarioArqueo;
}