package com.cibertec.pos_system.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Data
public class CajaVentaDTO {
    private Long id; 
    private Long cajaSesionId;
    private Long clienteId;
    private Long medioPagoId; 
    private String tipoComprobante;
    private BigDecimal subtotal;
    private BigDecimal descuento;  //  NUEVA LÍNEA
    private BigDecimal impuesto;
    private BigDecimal total;
    private List<CajaVentaDetalleDTO> detalles = new ArrayList<>(); 
    private String motivoAnulacion;
}
// numero de comprobante y estado