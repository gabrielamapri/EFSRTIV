package com.cibertec.pos_system.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "caja_sesion")
@Data
public class CajaSesionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "caja_id", nullable = false)
    private CajaEntity caja;

    // Usuario que abre la sesión
    @ManyToOne
    @JoinColumn(name = "usuario_apertura_id", nullable = false)
    private UsuarioEntity usuarioApertura;

    // Usuario que cierra la sesión
    @ManyToOne
    @JoinColumn(name = "usuario_cierre_id")
    private UsuarioEntity usuarioCierre;

    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "monto_inicial")
    private Double montoInicial;

    @Column(name = "monto_cierre")
    private Double montoCierre;

    @Column(name = "estado")
    private String estado;

    @OneToMany(mappedBy = "cajaSesion")
private List<CajaVentaEntity> ventas;

}