package com.cibertec.pos_system.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.math.BigDecimal;
@Entity
@Table(name = "productos")
@Data
@ToString(exclude = {"categoria", "proveedor"})
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagen;
    private boolean activo;
    private int stockActual;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private ProveedorEntity proveedor;
    @Transient
    private BigDecimal precioFinal;//natm
    @Transient
private DescuentoEntity descuentoAplicado;//natm
    @Transient
private BigDecimal montoDescuento;//natm
}
