package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.ClienteEntity;
import com.cibertec.pos_system.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/cliente") // ruta para vistas, distinta de /clientes para API
@RequiredArgsConstructor
public class ClienteViewController {

    private final ClienteService clienteService;

    @GetMapping("/{id}")
    public String verPerfil(@PathVariable Long id, Model model) {
        ClienteEntity cliente = clienteService.obtener(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));

        model.addAttribute("cliente", cliente);
        return "cliente/perfil"; // Vista HTML a renderizar
    }

    @GetMapping
    public String listarClientes(Model model) {
    List<ClienteEntity> clientes = clienteService.listar(); // puede ser vacía
    model.addAttribute("clientes", clientes);
    return "cliente/lista";
}
}
