package com.cibertec.pos_system.service;

import com.cibertec.pos_system.entity.RolEntity;
import com.cibertec.pos_system.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {
    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<RolEntity> listar() {
        return rolRepository.findAll();
    }

    public RolEntity guardar(RolEntity rol) {
        return rolRepository.save(rol);
    }

    public RolEntity actualizar(Long id, RolEntity rol) {
        rol.setId(id != null ? id.intValue() : null);
        return rolRepository.save(rol);
    }

    public void eliminar(Long id) {
        rolRepository.deleteById(id);
    }

    public Optional<RolEntity> obtener(Long id) {
        return rolRepository.findById(id);
    }
}
