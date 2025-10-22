package com.cibertec.pos_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "caja_venta_detalle")
@Data
public class CajaVentaDetalleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private CajaVentaEntity venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private ProductoEntity producto;

    private int cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    @Column(name = "subtotal")
    private BigDecimal subtotal;

    // PASO 2: AGREGAR ESTOS CAMPOS PARA DESCUENTOS
    @Column(name = "precio_original", precision = 10, scale = 2)
    private BigDecimal precioOriginal = BigDecimal.ZERO;
    
    @Column(name = "descuento_aplicado", precision = 10, scale = 2)
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;
    
    @Column(name = "tipo_descuento")
    private String tipoDescuento; // PORCENTAJE, FIJO, DOS_POR_UNO
}