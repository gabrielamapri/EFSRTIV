package com.cibertec.pos_system.service;

import com.cibertec.pos_system.entity.MedioPagoEntity;
import com.cibertec.pos_system.repository.MedioPagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedioPagoService {

    private final MedioPagoRepository medioPagoRepository;

    public MedioPagoService(MedioPagoRepository medioPagoRepository) {
        this.medioPagoRepository = medioPagoRepository;
    }

    public List<MedioPagoEntity> listar() {
        return medioPagoRepository.findAll();
    }

    public MedioPagoEntity guardar(MedioPagoEntity medioPago) {
        return medioPagoRepository.save(medioPago);
    }

    public MedioPagoEntity actualizar(Long id, MedioPagoEntity medioPago) {
        medioPago.setId(id);
        return medioPagoRepository.save(medioPago);
    }

    public void eliminar(Long id) {
        medioPagoRepository.deleteById(id);
    }

    public Optional<MedioPagoEntity> obtener(Long id) {
        return medioPagoRepository.findById(id);
    }
}
