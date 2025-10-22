package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.CompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CompraRepository extends JpaRepository<CompraEntity, Long> {

    List<CompraEntity> findByClienteDni(String dni);
    List<CompraEntity> findByClienteId(Long clienteId);
    Page<CompraEntity> findByClienteDni(String dni, Pageable pageable);

    @Query("SELECT COUNT(c) FROM CompraEntity c WHERE c.cliente.dni = :dni")
    long contarComprasPorCliente(@Param("dni") String dni);

    boolean existsByClienteId(Long clienteId);

    Page<CompraEntity> findByClienteId(Long clienteId, Pageable pageable);

}

