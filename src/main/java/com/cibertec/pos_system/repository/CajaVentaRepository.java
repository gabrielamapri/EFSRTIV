package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.CajaVentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CajaVentaRepository extends JpaRepository<CajaVentaEntity, Long> {
    @Query("SELECT v.numeroComprobante FROM CajaVentaEntity v WHERE v.tipoComprobante = :tipoComprobante AND v.cajaSesion.caja.local.id = :localId ORDER BY v.id DESC")
    List<String> findUltimoNumeroComprobantePorLocal(@Param("tipoComprobante") String tipoComprobante, @Param("localId") Long localId);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM CajaVentaEntity v WHERE v.cajaSesion.id = :cajaSesionId AND UPPER(v.medioPago.nombre) = 'EFECTIVO' AND v.estado = 'FINALIZADA'")
    BigDecimal totalVentasEfectivoPorSesion(@Param("cajaSesionId") Long cajaSesionId);

    @Query("SELECT v.id, v.total, COALESCE(c.nombre, 'Consumidor Final'), v.estado, v.fechaHora FROM CajaVentaEntity v LEFT JOIN v.cliente c WHERE v.cajaSesion.id = :cajaSesionId AND UPPER(v.medioPago.nombre) = 'TARJETA' AND v.estado = 'FINALIZADA'")
List<Object[]> findVentasTarjetaPorSesion(@Param("cajaSesionId") Long cajaSesionId);

@Query("SELECT v.id, v.total, COALESCE(c.nombre, 'Consumidor Final'), v.estado, v.fechaHora FROM CajaVentaEntity v LEFT JOIN v.cliente c WHERE v.cajaSesion.id = :cajaSesionId AND UPPER(v.medioPago.nombre) = 'EFECTIVO' AND v.estado = 'FINALIZADA'")
List<Object[]> findVentasEfectivoPorSesion(@Param("cajaSesionId") Long cajaSesionId);

List<CajaVentaEntity> findByCliente_Dni(String dni);
List<CajaVentaEntity> findByCliente_Id(Long id);

}