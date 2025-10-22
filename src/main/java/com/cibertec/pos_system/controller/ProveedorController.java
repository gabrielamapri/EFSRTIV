package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.ProveedorEntity;
import com.cibertec.pos_system.service.ProveedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public List<ProveedorEntity> listar() {
        return proveedorService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorEntity> obtener(@PathVariable Long id) {
        return proveedorService.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ProveedorEntity guardar(@RequestBody ProveedorEntity proveedor) {
        return proveedorService.guardar(proveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorEntity> actualizar(@PathVariable Long id, @RequestBody ProveedorEntity proveedor) {
        if (!proveedorService.obtener(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(proveedorService.actualizar(id, proveedor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!proveedorService.obtener(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        proveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
