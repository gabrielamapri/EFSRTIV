package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.DescuentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DescuentoRepository extends JpaRepository<DescuentoEntity, Long> {


    List<DescuentoEntity> findByActivoTrue();


    List<DescuentoEntity> findByFechaInicioAfter(LocalDate fecha);

    List<DescuentoEntity> findByFechaFinBefore(LocalDate fecha);

 
    @Query("SELECT d FROM DescuentoEntity d WHERE d.fechaInicio <= :hoy AND d.fechaFin >= :hoy AND d.activo = true")
    List<DescuentoEntity> findDescuentosVigentes(@Param("hoy") LocalDate hoy);
    

@Query("SELECT d FROM DescuentoEntity d WHERE d.activo = true AND " +
       "(d.producto.id = :productoId OR d.categoria.id = :categoriaId) " +
       "AND CURRENT_DATE BETWEEN d.fechaInicio AND d.fechaFin")
List<DescuentoEntity> findDescuentosActivos(@Param("productoId") Long productoId, @Param("categoriaId") Long categoriaId);


}
