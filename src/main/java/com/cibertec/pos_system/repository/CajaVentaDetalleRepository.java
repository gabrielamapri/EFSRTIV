package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.CajaVentaDetalleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CajaVentaDetalleRepository extends JpaRepository<CajaVentaDetalleEntity, Long> {
    // Métodos personalizados si necesitas búsquedas por venta, producto, etc.
}