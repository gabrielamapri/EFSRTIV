package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<VentaEntity, Long> {
}