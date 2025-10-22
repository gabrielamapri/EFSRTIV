package com.cibertec.pos_system.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CajaVentaDetalleDTO {
    private Long productoId;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}