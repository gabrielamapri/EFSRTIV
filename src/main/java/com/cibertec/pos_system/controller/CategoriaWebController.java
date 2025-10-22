package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.CategoriaEntity;
import com.cibertec.pos_system.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categoria")
public class CategoriaWebController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaService.listar());
        return "categoria/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevaCategoria(Model model) {
        model.addAttribute("categoria", new CategoriaEntity());
        return "categoria/formulario";
    }

    @PostMapping("/guardar")
    public String guardarCategoria(@ModelAttribute("categoria") CategoriaEntity categoria) {
        if (categoria.getId() == null) {
            categoriaService.crear(categoria);
        } else {
            categoriaService.actualizar(categoria.getId(), categoria);
        }
        return "redirect:/categoria";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarCategoria(@PathVariable Long id, Model model) {
        CategoriaEntity categoria = categoriaService.obtener(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        model.addAttribute("categoria", categoria);
        return "categoria/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return "redirect:/categoria";
    }
}
