package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.ProductoEntity;
import com.cibertec.pos_system.service.ProductoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoEntity> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    public ProductoEntity obtener(@PathVariable Long id) {
        return productoService.obtener(id)
                .orElseThrow(() -> new RuntimeException("ProductoEntity no encontrado"));
    }

    @PostMapping
    public ProductoEntity crear(@RequestBody ProductoEntity producto) {
        return productoService.crear(producto);
    }

    @PutMapping("/{id}")
    public ProductoEntity actualizar(@PathVariable Long id, @RequestBody ProductoEntity producto) {
        return productoService.actualizar(id, producto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }

   
}
