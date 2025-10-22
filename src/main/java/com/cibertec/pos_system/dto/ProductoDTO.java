package com.cibertec.pos_system.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private int stockActual;
}
