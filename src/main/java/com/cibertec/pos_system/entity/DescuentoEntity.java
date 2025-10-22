package com.cibertec.pos_system.entity;
import com.cibertec.pos_system.enums.TipoDescuento;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "descuentos")
@Data
public class DescuentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoDescuento tipo;

    private BigDecimal valor; // Porcentaje o monto fijo (según tipo)

    private boolean activo;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private BigDecimal condicionMinimaPrecio; // solo para aplicar si supera un mínimo (ej: > 100)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = true)
    private CategoriaEntity categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = true)
    private ProductoEntity producto;

    public boolean isProgramado() {
    return fechaInicio != null && fechaInicio.isAfter(LocalDate.now());
    }

    public boolean isEnCurso() {
    return fechaInicio != null && fechaFin != null &&
           ( !fechaInicio.isAfter(LocalDate.now()) && !fechaFin.isBefore(LocalDate.now()) );
    }


    public boolean isExpirado() {
    return fechaFin != null && fechaFin.isBefore(LocalDate.now());
    }

}
