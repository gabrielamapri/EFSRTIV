package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.RolEntity;
import com.cibertec.pos_system.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rol")
public class RolController {

    @Autowired
    private RolService rolService;

    /**
     * Muestra la lista de todos los roles
     */
    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("listaRoles", rolService.listar());
            return "rol/listar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la lista de roles: " + e.getMessage());
            model.addAttribute("listaRoles", java.util.Collections.emptyList());
            return "rol/listar";
        }
    }

    /**
     * Muestra el formulario para crear un nuevo rol
     */
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("rol", new RolEntity());
        model.addAttribute("accion", "/rol/guardar");
        return "rol/formulario";
    }

    /**
     * Muestra el formulario para editar un rol existente
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            RolEntity rol = rolService.obtener(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

            model.addAttribute("rol", rol);
            model.addAttribute("accion", "/rol/actualizar/" + id);
            return "rol/formulario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el rol: " + e.getMessage());
            return "redirect:/rol";
        }
    }

    /**
     * Guarda un nuevo rol
     */
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute RolEntity rol,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        // Preparar el modelo por si hay errores
        model.addAttribute("accion", "/rol/guardar");

        // Limpiar el ID para operaciones de creación
        rol.setId(null);

        // Validaciones personalizadas
        if (!validarNombreRol(rol, result, null)) {
            return "rol/formulario";
        }

        try {
            // Normalizar el nombre del rol
            rol.setNombre(rol.getNombre().trim().toUpperCase());

            rolService.guardar(rol);
            redirectAttributes.addFlashAttribute("success",
                    "El rol '" + rol.getNombre() + "' ha sido creado exitosamente");
            return "redirect:/rol";

        } catch (Exception e) {
            model.addAttribute("error", "Error inesperado al guardar el rol: " + e.getMessage());
            return "rol/formulario";
        }
    }

    /**
     * Actualiza un rol existente
     */
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute RolEntity rol,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        // Preparar el modelo por si hay errores
        model.addAttribute("accion", "/rol/actualizar/" + id);

        // Asegurar que el ID esté establecido
        rol.setId(id);

        // Validaciones personalizadas
        if (!validarNombreRol(rol, result, id)) {
            return "rol/formulario";
        }

        try {
            // Verificar que el rol existe
            RolEntity rolExistente = rolService.obtener(Integer.toUnsignedLong(id))
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

            // Verificar si es un rol del sistema y prevenir cambios críticos
            if (esRolDelSistema(rolExistente.getNombre()) &&
                    !rolExistente.getNombre().equalsIgnoreCase(rol.getNombre().trim())) {
                model.addAttribute("error",
                        "No se puede modificar el nombre de un rol del sistema");
                return "rol/formulario";
            }

            // Normalizar el nombre del rol
            rol.setNombre(rol.getNombre().trim().toUpperCase());

            rolService.actualizar(Integer.toUnsignedLong(id), rol);
            redirectAttributes.addFlashAttribute("success",
                    "El rol '" + rol.getNombre() + "' ha sido actualizado exitosamente");
            return "redirect:/rol";

        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el rol: " + e.getMessage());
            return "rol/formulario";
        }
    }

    /**
     * Elimina un rol
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Verificar si el rol existe
            RolEntity rol = rolService.obtener(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

            // No permitir eliminar roles del sistema
            if (esRolDelSistema(rol.getNombre())) {
                redirectAttributes.addFlashAttribute("error",
                        "No se puede eliminar el rol '" + rol.getNombre() +
                                "' porque es un rol del sistema");
                return "redirect:/rol";
            }

            String nombreRol = rol.getNombre();
            rolService.eliminar(id);
            redirectAttributes.addFlashAttribute("success",
                    "El rol '" + nombreRol + "' ha sido eliminado exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar el rol: " + e.getMessage());
        }

        return "redirect:/rol";
    }

    /**
     * Valida el nombre del rol
     */
    private boolean validarNombreRol(RolEntity rol, BindingResult result, Integer idExcluir) {
        // Validar que el nombre no esté vacío
        if (rol.getNombre() == null || rol.getNombre().trim().isEmpty()) {
            result.addError(new FieldError("rol", "nombre",
                    "El nombre del rol es obligatorio"));
            return false;
        }

        // Validar longitud mínima
        String nombre = rol.getNombre().trim();
        if (nombre.length() < 2) {
            result.addError(new FieldError("rol", "nombre",
                    "El nombre del rol debe tener al menos 2 caracteres"));
            return false;
        }

        // Validar longitud máxima
        if (nombre.length() > 50) {
            result.addError(new FieldError("rol", "nombre",
                    "El nombre del rol no puede tener más de 50 caracteres"));
            return false;
        }

        // Validar caracteres permitidos (solo letras, números y espacios)
        if (!nombre.matches("^[a-zA-Z0-9\\s_-]+$")) {
            result.addError(new FieldError("rol", "nombre",
                    "El nombre del rol solo puede contener letras, números, espacios, guiones y guiones bajos"));
            return false;
        }

        // Verificar si ya existe un rol con ese nombre
        String nombreNormalizado = nombre.toUpperCase();
        boolean existeNombre = (idExcluir == null) ?
                existeRolConNombre(nombreNormalizado) :
                existeRolConNombreExceptoId(nombreNormalizado, idExcluir);

        if (existeNombre) {
            result.addError(new FieldError("rol", "nombre",
                    "Ya existe un rol con el nombre '" + nombreNormalizado + "'"));
            return false;
        }

        return true;
    }

    /**
     * Verificar si existe un rol con el nombre dado
     */
    private boolean existeRolConNombre(String nombre) {
        try {
            return rolService.listar().stream()
                    .anyMatch(rol -> rol.getNombre().equalsIgnoreCase(nombre));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verificar si existe otro rol con el nombre dado (excluyendo el ID actual)
     */
    private boolean existeRolConNombreExceptoId(String nombre, Integer idExcluir) {
        try {
            return rolService.listar().stream()
                    .anyMatch(rol -> rol.getNombre().equalsIgnoreCase(nombre) &&
                            !rol.getId().equals(idExcluir));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verificar si es un rol del sistema que no se debe eliminar
     */
    private boolean esRolDelSistema(String nombre) {
        if (nombre == null) return false;

        String[] rolesDelSistema = {"USUARIOS", "ROLES"};
        for (String rolSistema : rolesDelSistema) {
            if (rolSistema.equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Manejo de errores específicos
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        return "redirect:/rol";
    }

    /**
     * Manejo de errores generales
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
                "Ha ocurrido un error inesperado. Por favor, inténtelo nuevamente.");
        return "redirect:/rol";
    }
}