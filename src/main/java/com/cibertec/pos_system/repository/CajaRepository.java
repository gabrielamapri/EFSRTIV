package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.CajaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CajaRepository extends JpaRepository<CajaEntity, Long> {

    @Query("SELECT c FROM CajaEntity c " +
           "LEFT JOIN c.local l " +
           "LEFT JOIN c.usuario u " +
           "WHERE LOWER(c.codigo) LIKE %:q% " +
           "OR LOWER(c.nombre) LIKE %:q% " +
           "OR LOWER(l.nombre) LIKE %:q% " +
           "OR CAST(c.fechaCreacion AS string) LIKE %:q% " +
           "OR LOWER(u.username) LIKE %:q%")
    List<CajaEntity> buscarPorCualquierCampo(@Param("q") String q);

    // Nuevo método para obtener el código máximo por local y prefijo
    @Query("SELECT MAX(c.codigo) FROM CajaEntity c WHERE c.local.id = :localId AND c.codigo LIKE CONCAT(:prefijo, '%')")
    String findMaxCodigoByLocalAndPrefijo(@Param("localId") Long localId, @Param("prefijo") String prefijo);
}