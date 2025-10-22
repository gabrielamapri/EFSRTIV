package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.ArqueoCajaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArqueoCajaRepository extends JpaRepository<ArqueoCajaEntity, Long> {
Optional<ArqueoCajaEntity> findTopByCajaSesionIdOrderByFechaArqueoDesc(Long sesionId);

}