package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.ProveedorEntity;
import com.cibertec.pos_system.service.ProveedorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/proveedor") 
public class ProveedorWebController {

    private final ProveedorService proveedorService;

    public ProveedorWebController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public String listarProveedores(Model model) {
        model.addAttribute("proveedores", proveedorService.listar());
        return "proveedor/lista"; 
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProveedor(@PathVariable Long id, Model model) {
        ProveedorEntity proveedor = proveedorService.obtener(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        model.addAttribute("proveedor", proveedor);
        return "proveedor/formulario";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProveedor(Model model) {
        model.addAttribute("proveedor", new ProveedorEntity());
        return "proveedor/formulario";
    }

    @PostMapping("/guardar")
    public String guardarProveedor(@ModelAttribute("proveedor") ProveedorEntity proveedor) {
        if (proveedor.getId() == null) {
            proveedorService.guardar(proveedor);
        } else {
            proveedorService.actualizar(proveedor.getId(), proveedor);
        }
        return "redirect:/proveedor";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminar(id);
        return "redirect:/proveedor";
    }
}
