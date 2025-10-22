package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.LocalEntity;
import com.cibertec.pos_system.service.LocalService;

import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/local")
public class LocalController {

    @Autowired
    private LocalService localService;

    @GetMapping
    public String listarLocales(Model model) {
        List<LocalEntity> locales = localService.listar();
        model.addAttribute("listaLocales", locales);
        return "local/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoLocal(Model model) {
        model.addAttribute("local", new LocalEntity());
        model.addAttribute("accion", "/local/nuevo");
        return "local/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarNuevoLocal(@ModelAttribute LocalEntity local) {
        localService.crear(local);
        return "redirect:/local";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        LocalEntity local = localService.obtener(id).orElseThrow(() -> new RuntimeException("Local no encontrado"));
        model.addAttribute("local", local);
        model.addAttribute("accion", "/local/editar/" + id);
        return "local/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarLocal(@PathVariable Long id, @ModelAttribute LocalEntity local) {
        localService.actualizar(id, local);
        return "redirect:/local";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarLocal(@PathVariable Long id) {
        localService.eliminar(id);
        return "redirect:/local";
    }
}