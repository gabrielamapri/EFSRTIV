package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.ProveedorEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {
    Optional<ProveedorEntity> findByRuc(String ruc);
}
