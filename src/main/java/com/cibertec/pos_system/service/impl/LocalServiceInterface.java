package com.cibertec.pos_system.service.impl;

import java.util.List;
import java.util.Optional;

import com.cibertec.pos_system.entity.LocalEntity;

public interface LocalServiceInterface {
    List<LocalEntity> listar();

    LocalEntity obtenerPorId(Long id);

    LocalEntity crear(LocalEntity local);

    LocalEntity actualizar(Long id, LocalEntity local);

    void eliminar(Long id);

    long contar();

    Optional<LocalEntity> obtener(Long id);
}