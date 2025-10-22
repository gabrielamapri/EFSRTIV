package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.CategoriaEntity;
import com.cibertec.pos_system.service.CategoriaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaEntity> listar() {
        return categoriaService.listar();
    }

    @PostMapping
    public CategoriaEntity crear(@RequestBody CategoriaEntity categoria) {
        return categoriaService.crear(categoria);
    }

    @PutMapping("/{id}")
    public CategoriaEntity actualizar(@PathVariable Long id, @RequestBody CategoriaEntity categoria) {
        return categoriaService.actualizar(id, categoria);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
    }

    @GetMapping("/{id}")
    public CategoriaEntity obtener(@PathVariable Long id) {
        return categoriaService.obtener(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }
}
