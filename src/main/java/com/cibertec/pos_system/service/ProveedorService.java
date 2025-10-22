package com.cibertec.pos_system.service;

import com.cibertec.pos_system.entity.ProveedorEntity;
import com.cibertec.pos_system.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<ProveedorEntity> listar() {
        return proveedorRepository.findAll();
    }

    public ProveedorEntity guardar(ProveedorEntity proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public ProveedorEntity actualizar(Long id, ProveedorEntity proveedor) {
        proveedor.setId(id);
        return proveedorRepository.save(proveedor);
    }

    public void eliminar(Long id) {
        proveedorRepository.deleteById(id);
    }

    public Optional<ProveedorEntity> obtener(Long id) {
        return proveedorRepository.findById(id);
    }

     public ProveedorEntity obtenerProveedorPorRuc(String ruc) {
        return proveedorRepository.findByRuc(ruc).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con RUC: " + ruc));
    }
}
