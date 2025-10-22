package com.cibertec.pos_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cibertec.pos_system.entity.ClienteEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByDni(String dni);

    Page<ClienteEntity> findByNombreStartingWithIgnoreCase(String letra, Pageable pageable);
    List<ClienteEntity> findByDniContainingIgnoreCase(String dni);
    
    // Method for DNI search with pagination 
    Page<ClienteEntity> findByDniContaining(String dni, Pageable pageable);
}