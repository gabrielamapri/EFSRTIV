package com.cibertec.pos_system.service.impl;

import com.cibertec.pos_system.entity.ClienteEntity;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<ClienteEntity> listar();
    ClienteEntity guardar(ClienteEntity cliente);
    ClienteEntity actualizar(Long id, ClienteEntity cliente);
    void eliminar(Long id);
    Optional<ClienteEntity> obtener(Long id);
}
