package com.cibertec.pos_system.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cibertec.pos_system.entity.UsuarioEntity;

public interface UsuarioServiceInterface {
    List<UsuarioEntity> obtenerTodas();

    UsuarioEntity obtenerPorId(Long id);

    UserDetails obtenerPorUsuario(String username) throws UsernameNotFoundException;

    UsuarioEntity crearUser(UsuarioEntity user);

    UsuarioEntity actualizarUser(Long id, UsuarioEntity user);

    void eliminarUser(Long id);

    long contarUsers();

    Optional<UsuarioEntity> obtener(Long id);
}
