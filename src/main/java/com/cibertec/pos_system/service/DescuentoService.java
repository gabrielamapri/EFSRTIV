

 package com.cibertec.pos_system.service;

import com.cibertec.pos_system.entity.DescuentoEntity;
import com.cibertec.pos_system.entity.ProductoEntity;

import java.math.BigDecimal;
import java.util.List;

public interface DescuentoService {

    // Listar descuentos activos para mostrar
    List<DescuentoEntity> listarActivos();

    // Listar todos los descuentos pasados y programados
    List<DescuentoEntity> listarHistorial();

    // Guardar nuevo descuento
    DescuentoEntity guardar(DescuentoEntity descuento);

    // Desactivar descuento manualmente
    void desactivar(Long id);

    // Obtener el descuento aplicable a un producto (por producto o categoría)
    DescuentoEntity obtenerDescuentoAplicable(ProductoEntity producto);

    // Calcular el precio final del producto con el descuento aplicado
    BigDecimal calcularPrecioConDescuento(ProductoEntity producto);

    
}
