package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.CajaSesionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CajaSesionRepository extends JpaRepository<CajaSesionEntity, Long> {
    List<CajaSesionEntity> findByCajaIdOrderByFechaAperturaDesc(Long cajaId);
    List<CajaSesionEntity> findByEstado(String estado);

    // Devuelve la sesión activa (estado ABIERTA) de una caja
    @Query("SELECT s FROM CajaSesionEntity s WHERE s.caja.id = :cajaId AND s.estado = 'ABIERTA'")
    CajaSesionEntity findSesionActivaByCajaId(@Param("cajaId") Long cajaId);

    // Búsqueda por cualquier campo (caja, local, usuario apertura/cierre, estado, fecha apertura/cierre)
    @Query("SELECT s FROM CajaSesionEntity s " +
           "LEFT JOIN s.caja c " +
           "LEFT JOIN c.local l " +
           "LEFT JOIN s.usuarioApertura ua " +
           "LEFT JOIN s.usuarioCierre uc " +
           "WHERE LOWER(c.nombre) LIKE %:q% " +
           "OR LOWER(l.nombre) LIKE %:q% " +
           "OR LOWER(ua.username) LIKE %:q% " +
           "OR LOWER(uc.username) LIKE %:q% " +
           "OR LOWER(s.estado) LIKE %:q% " +
           "OR CAST(s.fechaApertura AS string) LIKE %:q% " +
           "OR CAST(s.fechaCierre AS string) LIKE %:q%")
    List<CajaSesionEntity> buscarPorCualquierCampo(@Param("q") String q);

    // --- AGREGADO: Traer sesión con ventas (fetch join) ---
    @Query("SELECT s FROM CajaSesionEntity s LEFT JOIN FETCH s.ventas WHERE s.id = :id")
    CajaSesionEntity findByIdWithVentas(@Param("id") Long id);
    
    // Agrega esta línea al final de tu CajaSesionRepository
CajaSesionEntity findByUsuarioAperturaIdAndEstado(Long usuarioId, String estado);
}