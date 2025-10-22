package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.RolEntity;
import com.cibertec.pos_system.entity.UsuarioEntity;
import com.cibertec.pos_system.service.RolService;
import com.cibertec.pos_system.service.UsuarioService;

import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UsuarioController {

    @Autowired
    private UsuarioService userService;
    @Autowired
    private RolService roleService;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<UsuarioEntity> users = userService.obtenerTodas();
        model.addAttribute("listaUsers",users);
        return "user/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoUsuario(Model model) {
        model.addAttribute("user", new UsuarioEntity());
        model.addAttribute("listaRoles", roleService.listar()); // Se pasa la lista de roles al formulario
        model.addAttribute("accion","/user/nuevo"); // Se define la acción del formulario ( Ruta que se ejecutara cuando guarde los cambios)
        return "user/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarNuevoUsuario(@ModelAttribute UsuarioEntity user, @RequestParam("roles") List<Long> rolIds) {
        // Obtener los roles seleccionados
        Set<RolEntity> roles = new HashSet<>();
        for (Long id : rolIds) {
            // Buscamos el rol en la BD
            RolEntity rol = roleService.obtener(id).orElse(null);
            if (rol != null) {
                // SI existe se agrega
                roles.add(rol);
            }
        }

        user.setRoles(roles);
        userService.crearUser(user);

        return "redirect:/user";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        UsuarioEntity user = userService.obtenerPorId(id);
        model.addAttribute("user",user);
        model.addAttribute("listaRoles", roleService.listar());  // Se pasa la lista de roles al formulario
        model.addAttribute("accion","/user/editar/"+id);
        return "user/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
                                    @ModelAttribute UsuarioEntity user,
                                    @RequestParam("roles") List<Long> rolIds) {
        // Obtener los roles seleccionados
        Set<RolEntity> roles = new HashSet<>();
        for (Long rolId : rolIds) {
            // Buscamos el rol en la BD
            RolEntity rol = roleService.obtener(rolId).orElse(null);
            if (rol != null) {
                // SI existe se agrega
                roles.add(rol);
            }
        }

        user.setRoles(roles);
        userService.actualizarUser(id, user);

        return "redirect:/user";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id){
        userService.eliminarUser(id);
        return "redirect:/user";
    }
}