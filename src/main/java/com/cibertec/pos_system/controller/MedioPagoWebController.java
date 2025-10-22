package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.MedioPagoEntity;
import com.cibertec.pos_system.service.MedioPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/medioPago")
public class MedioPagoWebController {

    @Autowired
    private MedioPagoService medioPagoService;

    @GetMapping
    public String listarMediosPago(Model model) {
        model.addAttribute("listaMediosPago", medioPagoService.listar());
        return "medioPago/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("medioPago", new MedioPagoEntity());
        model.addAttribute("accion", "/medioPago/nuevo");
        return "medioPago/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarNuevo(@ModelAttribute MedioPagoEntity medioPago) {
        medioPagoService.guardar(medioPago);
        return "redirect:/medioPago";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        MedioPagoEntity medioPago = medioPagoService.obtener(id).orElseThrow(() -> new RuntimeException("No encontrado"));
        model.addAttribute("medioPago", medioPago);
        model.addAttribute("accion", "/medioPago/editar/" + id);
        return "medioPago/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute MedioPagoEntity medioPago) {
        medioPagoService.actualizar(id, medioPago);
        return "redirect:/medioPago";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        medioPagoService.eliminar(id);
        return "redirect:/medioPago";
    }
}
