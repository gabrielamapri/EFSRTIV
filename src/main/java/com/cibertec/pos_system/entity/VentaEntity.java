package com.cibertec.pos_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
@Data
public class VentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    private BigDecimal total;
    private LocalDateTime fecha;

    private boolean recibePuntos; // ¿Acepta puntos esta compra?
    /*Todo esta entidad se encarga de las compras que hacen los clientes, no la que hace la tienda */

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalleEntity> detalles;
}