package com.cibertec.pos_system.service;

import com.cibertec.pos_system.entity.CajaEntity;
import com.cibertec.pos_system.repository.CajaRepository;
import com.cibertec.pos_system.service.impl.CajaServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CajaService implements CajaServiceInterface {

    private final CajaRepository cajaRepository;

    public CajaService(CajaRepository cajaRepository) {
        this.cajaRepository = cajaRepository;
    }

    @Override
    public List<CajaEntity> listar() {
        return cajaRepository.findAll();
    }

    @Override
    public CajaEntity crear(CajaEntity caja) {
        return cajaRepository.save(caja);
    }

    @Override
    public CajaEntity actualizar(Long id, CajaEntity caja) {
        CajaEntity existente = cajaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));
        caja.setFechaCreacion(existente.getFechaCreacion());
        caja.setUsuario(existente.getUsuario());
        caja.setId(id);
        return cajaRepository.save(caja);
    }

    @Override
    public void eliminar(Long id) {
        cajaRepository.deleteById(id);
    }

    @Override
    public Optional<CajaEntity> obtener(Long id) {
        return cajaRepository.findById(id);
    }

    @Override
    public long contar() {
        return cajaRepository.count();
    }

    @Override
    public CajaEntity obtenerPorId(Long id) {
        return cajaRepository.findById(id).orElse(null);
    }

    @Override
    public List<CajaEntity> buscarPorCualquierCampo(String q) {
        // Debes tener este método en tu repository, aquí es solo un ejemplo:
        return cajaRepository.buscarPorCualquierCampo(q);
    }
@Override
public String obtenerUltimoCodigoPorLocalYPrefijo(Long localId, String prefijo) {
    return cajaRepository.findMaxCodigoByLocalAndPrefijo(localId, prefijo);
}
    
}