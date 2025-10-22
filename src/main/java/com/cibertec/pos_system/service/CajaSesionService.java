package com.cibertec.pos_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cibertec.pos_system.entity.CajaSesionEntity;
import com.cibertec.pos_system.entity.UsuarioEntity;
import com.cibertec.pos_system.repository.CajaSesionRepository;
import org.springframework.transaction.annotation.Transactional;
@Service
public class CajaSesionService {
    private final CajaSesionRepository repository;

    public CajaSesionService(CajaSesionRepository repository) {
        this.repository = repository;
    }

    public List<CajaSesionEntity> listar() {
        return repository.findAll();
    }

    public Optional<CajaSesionEntity> obtener(Long id) {
        return repository.findById(id);
    }

    // MÉTODO AGREGADO PARA COMPATIBILIDAD CON EL CONTROLADOR
    public CajaSesionEntity obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public CajaSesionEntity guardar(CajaSesionEntity sesion) {
        return repository.save(sesion);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public List<CajaSesionEntity> listarPorCaja(Long cajaId) {
        return repository.findByCajaIdOrderByFechaAperturaDesc(cajaId);
    }

    public List<CajaSesionEntity> listarAbiertas() {
        return repository.findByEstado("ABIERTA");
    }

    // Método para obtener la sesión activa de una caja
    public CajaSesionEntity obtenerSesionActivaPorCajaId(Long cajaId) {
        return repository.findSesionActivaByCajaId(cajaId);
    }

    // Método para búsqueda por cualquier campo
    public List<CajaSesionEntity> buscarPorCualquierCampo(String q) {
        return repository.buscarPorCualquierCampo(q.toLowerCase());
    }

   public Optional<CajaSesionEntity> obtenerConVentas(Long id) {
    CajaSesionEntity sesion = repository.findByIdWithVentas(id);
    return Optional.ofNullable(sesion);
}

    @Transactional
public CajaSesionEntity actualizarUsuarioCierre(Long sesionId, UsuarioEntity usuarioCierre) {
    CajaSesionEntity sesion = repository.findById(sesionId).orElseThrow();
    sesion.setUsuarioCierre(usuarioCierre);
    return repository.save(sesion);
}

public CajaSesionEntity buscarSesionActivaPorUsuario(Long usuarioId) {
    return repository.findByUsuarioAperturaIdAndEstado(usuarioId, "ABIERTA");
}
}