package com.cibertec.pos_system.service.impl;

import com.cibertec.pos_system.entity.CajaEntity;
import java.util.List;
import java.util.Optional;

public interface CajaServiceInterface {
    List<CajaEntity> listar();

    CajaEntity obtenerPorId(Long id);

    CajaEntity crear(CajaEntity caja);

    CajaEntity actualizar(Long id, CajaEntity caja);

    void eliminar(Long id);

    long contar();

    Optional<CajaEntity> obtener(Long id);

    // Método para búsqueda por cualquier campo
    List<CajaEntity> buscarPorCualquierCampo(String q);

    String obtenerUltimoCodigoPorLocalYPrefijo(Long localId, String prefijo);
}