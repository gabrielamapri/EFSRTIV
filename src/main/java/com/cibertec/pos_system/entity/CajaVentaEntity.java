package com.cibertec.pos_system.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "caja_venta")
@Data
public class CajaVentaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "subtotal")
    private BigDecimal subtotal;

    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "impuesto")
    private BigDecimal impuesto;

    @Column(name = "total")
    private BigDecimal total;

    // PASO 1: AGREGAR ESTOS CAMPOS PARA DESCUENTOS
    @Column(name = "total_descuentos", precision = 10, scale = 2)
    private BigDecimal totalDescuentos = BigDecimal.ZERO;
    
    @Column(name = "subtotal_sin_descuento", precision = 10, scale = 2) 
    private BigDecimal subtotalSinDescuento = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medio_pago_id")
    private MedioPagoEntity medioPago;

    @Column(name = "tipo_comprobante")
    private String tipoComprobante; // boleta, factura, etc.

    @Column(name = "numero_comprobante")
    private String numeroComprobante; // secuencial

    @Column(name = "estado")
    private String estado; // FINALIZADA, ANULADA, etc.

    @Column(name = "motivo_anulacion")
    private String motivoAnulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_sesion_id")
    private CajaSesionEntity cajaSesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario; // Cajero

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_actualizacion_id")
    private UsuarioEntity usuarioActualizacion;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CajaVentaDetalleEntity> detalles;
}