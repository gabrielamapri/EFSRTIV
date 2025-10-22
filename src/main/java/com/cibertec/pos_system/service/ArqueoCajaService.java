package com.cibertec.pos_system.service;

import com.cibertec.pos_system.entity.ArqueoCajaEntity;
import com.cibertec.pos_system.repository.ArqueoCajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class ArqueoCajaService {

    @Autowired
    private ArqueoCajaRepository arqueoCajaRepository;

    public ArqueoCajaEntity guardar(ArqueoCajaEntity arqueo) {
        return arqueoCajaRepository.save(arqueo);
    }

    public List<ArqueoCajaEntity> listarPorCajaSesion(Long cajaSesionId) {
        return arqueoCajaRepository.findAll()
                .stream()
                .filter(a -> a.getCajaSesion().getId().equals(cajaSesionId))
                .toList();
    }

    public Optional<ArqueoCajaEntity> obtenerUltimoPorSesion(Long sesionId) {
    return arqueoCajaRepository.findTopByCajaSesionIdOrderByFechaArqueoDesc(sesionId);
}
}