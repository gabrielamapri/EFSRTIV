package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.MedioPagoEntity;
import com.cibertec.pos_system.service.MedioPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medios-pago")
public class MedioPagoController {

    private final MedioPagoService medioPagoService;

    public MedioPagoController(MedioPagoService medioPagoService) {
        this.medioPagoService = medioPagoService;
    }

    @GetMapping
    public List<MedioPagoEntity> listar() {
        return medioPagoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedioPagoEntity> obtener(@PathVariable Long id) {
        return medioPagoService.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MedioPagoEntity guardar(@RequestBody MedioPagoEntity medioPago) {
        return medioPagoService.guardar(medioPago);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedioPagoEntity> actualizar(@PathVariable Long id, @RequestBody MedioPagoEntity medioPago) {
        if (!medioPagoService.obtener(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(medioPagoService.actualizar(id, medioPago));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!medioPagoService.obtener(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        medioPagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
