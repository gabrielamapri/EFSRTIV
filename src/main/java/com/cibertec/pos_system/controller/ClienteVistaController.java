package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.ClienteEntity;
import com.cibertec.pos_system.repository.ClienteRepository;
import com.cibertec.pos_system.repository.CompraRepository;
import com.cibertec.pos_system.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/clientesVista")
public class ClienteVistaController {

    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;
    private final CompraRepository compraRepository;

    public ClienteVistaController(ClienteService clienteService, CompraRepository compraRepository, ClienteRepository clienteRepository) {
        this.clienteService = clienteService;
        this.compraRepository = compraRepository; // <-- y aquí
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/nuevo")
    public String nuevoCliente(Model model) {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setActivo(true); // Por defecto, clientes nuevos están activos
        model.addAttribute("cliente", cliente);
        return "cliente/registro"; // Esto busca cliente.html en /resources/templates
    }

    @PostMapping("/guardar")
    public String guardarCliente(@Valid @ModelAttribute("cliente") ClienteEntity cliente,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cliente/registro"; // vuelve al formulario con errores
        }
        
        // Validar que el DNI no exista ya (solo para clientes nuevos)
        if (cliente.getId() == null) {
            cliente.setActivo(true); // Asegurar que clientes nuevos estén activos
            Optional<ClienteEntity> clienteExistente = clienteService.buscarPorDni(cliente.getDni());
            if (clienteExistente.isPresent()) {
                model.addAttribute("errorDni", "Ya existe un cliente registrado con el DNI: " + cliente.getDni());
                return "cliente/registro";
            }
        } else {
            // Para edición, verificar que el DNI no exista en otro cliente
            Optional<ClienteEntity> clienteExistente = clienteService.buscarPorDni(cliente.getDni());
            if (clienteExistente.isPresent() && !clienteExistente.get().getId().equals(cliente.getId())) {
                model.addAttribute("errorDni", "Ya existe otro cliente registrado con el DNI: " + cliente.getDni());
                return "cliente/registro";
            }
        }
        
        System.out.println("Guardando cliente: " + cliente.getNombre() + " " + cliente.getApellido());
        ClienteEntity clienteGuardado = clienteService.guardar(cliente);
        System.out.println("Cliente guardado con ID: " + clienteGuardado.getId());
        
        redirectAttributes.addFlashAttribute("mensaje", "Cliente guardado exitosamente");
        return "redirect:/clientesVista/listado?page=0"; // Ir a la primera página
    }

    @GetMapping("/listado")
    public String listarClientes(
            @RequestParam(name = "filtro", required = false) String filtro,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "9") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ClienteEntity> clientesPage;

        if (filtro != null && !filtro.trim().isEmpty()) {
            clientesPage = clienteService.buscarPorDni(filtro, pageable);

            if (clientesPage.hasContent()) {
                model.addAttribute("mensaje", "Cliente(s) encontrado(s) por DNI: " + filtro);
            } else {
                model.addAttribute("mensajeError", "No se encontró cliente con DNI: " + filtro);
            }

        } else {
            clientesPage = clienteService.listarPaginado(pageable);
        }

        // ✅ Debug: imprimir cantidad de clientes
        System.out.println("Total de clientes encontrados: " + clientesPage.getTotalElements());
        System.out.println("Clientes en esta página: " + clientesPage.getContent().size());
        System.out.println("Página actual: " + clientesPage.getNumber());
        System.out.println("Total de páginas: " + clientesPage.getTotalPages());

        // ✅ Asegurarse que no sea null antes de usar .getContent()
        model.addAttribute("listaClientes", clientesPage.getContent());
        model.addAttribute("clientes", clientesPage.getContent()); // mantener ambas por compatibilidad
        model.addAttribute("currentPage", clientesPage.getNumber());
        model.addAttribute("totalPages", clientesPage.getTotalPages());
        model.addAttribute("totalElements", clientesPage.getTotalElements());
        model.addAttribute("filtro", filtro);

    return "cliente/listado";
    }






    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        ClienteEntity cliente = clienteService.obtener(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de cliente inválido: " + id));
        model.addAttribute("cliente", cliente);
        return "cliente/nuevo"; // reutilizamos el mismo formulario
    }
    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable("id") Long id, RedirectAttributes redirect) {
        if (compraRepository.existsByClienteId(id)) {
            redirect.addFlashAttribute("mensajeError", "No se puede eliminar el cliente porque tiene historial de compras.");
        } else {
            clienteService.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Cliente eliminado correctamente.");
        }
        return "redirect:/clientesVista/listado";
    }


    @GetMapping
    public String redireccionarListado() {
        return "redirect:/clientesVista/listado";
    }


}