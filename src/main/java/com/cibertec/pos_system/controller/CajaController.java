package com.cibertec.pos_system.controller;
import com.cibertec.pos_system.entity.CajaEntity;
import com.cibertec.pos_system.entity.CajaSesionEntity;
import com.cibertec.pos_system.entity.UsuarioEntity;
import com.cibertec.pos_system.entity.ClienteEntity;
import com.cibertec.pos_system.entity.LocalEntity;
import com.cibertec.pos_system.entity.MedioPagoEntity;
import com.cibertec.pos_system.repository.LocalRepository;
import com.cibertec.pos_system.repository.UsuarioRepository;
import com.cibertec.pos_system.service.CajaSesionService;
import com.cibertec.pos_system.service.CategoriaService;
import com.cibertec.pos_system.service.ProductoService;
import com.cibertec.pos_system.service.ClienteService;
import com.cibertec.pos_system.service.MedioPagoService;
import com.cibertec.pos_system.service.impl.CajaServiceInterface;
import com.cibertec.pos_system.service.impl.LocalServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;                    // <-- AGREGAR ESTA LÍNEA
import java.util.stream.Collectors; 

@Controller
@RequestMapping("/caja")
public class CajaController {

    private final CajaServiceInterface cajaService;
    private final LocalServiceInterface localService;
    private final CajaSesionService cajaSesionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private MedioPagoService medioPagoService;

    @Autowired
    private LocalRepository localRepository;


    public CajaController(
            CajaServiceInterface cajaService,
            LocalServiceInterface localService,
            CajaSesionService cajaSesionService) {
        this.cajaService = cajaService;
        this.localService = localService;
        this.cajaSesionService = cajaSesionService;
    }

    @GetMapping("/nuevo")
public String mostrarFormularioNuevo(Model model) {
    model.addAttribute("caja", new CajaEntity());
    model.addAttribute("locales", localService.listar());
    model.addAttribute("accion", "/caja/nuevo");
    return "caja/formulario";
}

    @GetMapping
    public String listar(@RequestParam(value = "q", required = false) String q, Model model) {
    // Obtener usuario autenticado
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String usuarioActual = authentication.getName();
    UsuarioEntity usuarioSesion = usuarioRepository.getUserByUsername(usuarioActual);

    // Verificar si es ADMIN
    boolean esAdmin = authentication.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

    // Obtener todas las sesiones
    List<CajaSesionEntity> sesiones = cajaSesionService.listar();

    // Buscar si el usuario tiene una caja abierta
    CajaSesionEntity sesionActivaUsuario = sesiones.stream()
        .filter(s -> s.getUsuarioApertura() != null
            && s.getUsuarioApertura().getId().equals(usuarioSesion.getId())
            && "ABIERTA".equals(s.getEstado()))
        .findFirst()
        .orElse(null);

    // LÓGICA PRINCIPAL: Filtrar cajas según el estado del usuario
    List<CajaEntity> cajas;
    if (sesionActivaUsuario != null && !esAdmin) {
        // Si el usuario tiene caja abierta Y NO es admin, solo mostrar esa caja
        cajas = List.of(sesionActivaUsuario.getCaja());
    } else {
        // Obtener todas las cajas según búsqueda
        List<CajaEntity> todasLasCajas;
        if (q != null && !q.isEmpty()) {
            todasLasCajas = cajaService.buscarPorCualquierCampo(q);
        } else {
            todasLasCajas = cajaService.listar();
        }
        
        if (esAdmin) {
            // ADMIN ve todas las cajas
            cajas = todasLasCajas;
        } else {
            // Usuario normal ve SOLO cajas cerradas (sin sesión activa)
            Set<Long> cajasAbiertas = sesiones.stream()
                .filter(s -> "ABIERTA".equals(s.getEstado()))
                .map(s -> s.getCaja().getId())
                .collect(Collectors.toSet());
            
            cajas = todasLasCajas.stream()
                .filter(caja -> !cajasAbiertas.contains(caja.getId()))
                .collect(Collectors.toList());
        }
    }

    // Crear mapas de sesiones activas
    Map<Long, CajaSesionEntity> sesionesActivas = new HashMap<>();
    Map<Long, Integer> sesionesPorCaja = new HashMap<>();

    for (CajaSesionEntity sesion : sesiones) {
        Long cajaId = sesion.getCaja().getId();
        if ("ABIERTA".equals(sesion.getEstado())) {
            sesionesActivas.put(cajaId, sesion);
        }
        sesionesPorCaja.put(cajaId, sesionesPorCaja.getOrDefault(cajaId, 0) + 1);
    }

    
    boolean usuarioTieneCajaAbierta = sesionActivaUsuario != null && !esAdmin;

    model.addAttribute("cajas", cajas);
    model.addAttribute("locales", localService.listar());
    model.addAttribute("sesionesActivas", sesionesActivas);
    model.addAttribute("sesionesPorCaja", sesionesPorCaja);
    model.addAttribute("usuarioTieneCajaAbierta", usuarioTieneCajaAbierta);
    model.addAttribute("esAdmin", esAdmin);

    return "caja/cajas";
}

    @GetMapping("/siguiente-codigo")
    @ResponseBody
    public Map<String, String> siguienteCodigo(@RequestParam Long localId) {
        // Lógica para obtener el local y generar el código y nombre sugeridos
        LocalEntity local = localRepository.findById(localId).orElse(null);
        if (local == null) return Map.of();

        // Obtener las dos primeras letras de la tercera palabra del nombre del local
        String[] palabras = local.getNombre().split("\\s+");
        String prefijo = palabras.length >= 3 ? palabras[2].substring(0, 2).toUpperCase() : "XX";

        // Buscar el último número usado para ese local y prefijo
        String maxCodigo = cajaService.obtenerUltimoCodigoPorLocalYPrefijo(localId, prefijo);
        int siguienteNumero = 1;
        if (maxCodigo != null && maxCodigo.length() > 2) {
            try {
                siguienteNumero = Integer.parseInt(maxCodigo.substring(2)) + 1;
            } catch (NumberFormatException ignored) {}
        }
        String numero = String.format("%02d", siguienteNumero);
        String codigo = prefijo + numero;
        String nombre = "Caja " + numero;

        return Map.of("codigo", codigo, "nombre", nombre);
    }

    @PostMapping("/nuevo")
    public String guardarNuevaCaja(@ModelAttribute CajaEntity caja) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        UsuarioEntity usuarioSesion = usuarioRepository.getUserByUsername(usuarioActual);
        caja.setUsuario(usuarioSesion);
        caja.setFechaCreacion(LocalDateTime.now());
        cajaService.crear(caja);
        return "redirect:/caja";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        List<CajaSesionEntity> sesiones = cajaSesionService.listar();
        boolean cajaAbierta = sesiones.stream()
                .anyMatch(s -> s.getCaja().getId().equals(id) && "ABIERTA".equals(s.getEstado()));
        if (cajaAbierta) {
            redirectAttributes.addFlashAttribute("errorEditar", "No se puede editar una caja abierta.");
            return "redirect:/caja";
        }
        CajaEntity caja = cajaService.obtener(id).orElseThrow(() -> new RuntimeException("Caja no encontrada"));
        model.addAttribute("caja", caja);
        model.addAttribute("locales", localService.listar());
        model.addAttribute("accion", "/caja/editar/" + id);
        return "caja/formulario";
    }

    @PostMapping("/editar/{id}")
    public String editarCaja(@PathVariable Long id, @ModelAttribute CajaEntity caja, RedirectAttributes redirectAttributes) {
        cajaService.actualizar(id, caja);
        redirectAttributes.addFlashAttribute("mensaje", "Caja actualizada correctamente.");
        return "redirect:/caja";
    }

   @GetMapping("/eliminar/{id}")
public String eliminarCaja(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    List<CajaSesionEntity> sesiones = cajaSesionService.listar();
    boolean cajaAbierta = sesiones.stream()
            .anyMatch(s -> s.getCaja().getId().equals(id) && "ABIERTA".equals(s.getEstado()));
    if (cajaAbierta) {
        redirectAttributes.addFlashAttribute("errorEliminar", "No se puede eliminar una caja abierta.");
        return "redirect:/caja";
    }
    try {
        // Eliminar todas las sesiones asociadas a la caja antes de eliminar la caja
        List<CajaSesionEntity> sesionesDeCaja = cajaSesionService.listarPorCaja(id);
        for (CajaSesionEntity sesion : sesionesDeCaja) {
            cajaSesionService.eliminar(sesion.getId());
        }
        cajaService.eliminar(id);
        redirectAttributes.addFlashAttribute("exitoEliminar", "Caja eliminada correctamente.");
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
        redirectAttributes.addFlashAttribute("errorEliminar", "No se puede eliminar, tiene registros asociados.");
    }
    return "redirect:/caja";
}

    // Métodos API REST (opcional, si los necesitas)
    @PostMapping
    @ResponseBody
    public CajaEntity crear(@RequestBody CajaEntity caja) {
        caja.setFechaCreacion(LocalDateTime.now());
        return cajaService.crear(caja);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public CajaEntity actualizar(@PathVariable Long id, @RequestBody CajaEntity caja) {
        return cajaService.actualizar(id, caja);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void eliminar(@PathVariable Long id) {
        cajaService.eliminar(id);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public CajaEntity obtener(@PathVariable Long id) {
        return cajaService.obtener(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));
    }

    // Endpoint para la vista del carrito, asociando caja y cliente
    @GetMapping("/carrito")
    public String mostrarCarrito(
            @RequestParam(required = false) Long cajaId,
            @RequestParam(required = false) Long clienteId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        System.out.println(">>> ENTRÓ AL MÉTODO mostrarCarrito");
        if (cajaId == null) {
            redirectAttributes.addFlashAttribute("errorCarrito", "Debe seleccionar una caja para iniciar la venta.");
            return "redirect:/caja";
        }
        CajaEntity caja = cajaService.obtener(cajaId).orElse(null);
        ClienteEntity cliente = null;
        if (clienteId != null) {
            cliente = clienteService.obtener(clienteId).orElse(null);
            if (cliente == null) {
                redirectAttributes.addFlashAttribute("errorCarrito", "No se encontró el cliente.");
                return "redirect:/caja";
            }
        }
        if (caja == null) {
            redirectAttributes.addFlashAttribute("errorCarrito", "No se encontró la caja.");
            return "redirect:/caja";
        }

        // Obtener la sesión activa de la caja
        CajaSesionEntity cajaSesion = cajaSesionService.listar().stream()
            .filter(s -> s.getCaja().getId().equals(cajaId) && "ABIERTA".equals(s.getEstado()))
            .findFirst()
            .orElse(null);

        if (cajaSesion == null) {
            redirectAttributes.addFlashAttribute("errorCarrito", "No hay sesión activa para esta caja.");
            return "redirect:/caja";
        }

        // Filtrar solo los medios de pago activos usando el método listar()
        List<MedioPagoEntity> mediosPagoActivos = medioPagoService.listar()
            .stream()
            .filter(MedioPagoEntity::isActivo)
            .collect(Collectors.toList());

        model.addAttribute("caja", caja);
        model.addAttribute("cajaSesion", cajaSesion); // <--- CORRECTO: ahora se pasa la sesión activa
        model.addAttribute("cliente", cliente);
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("mediosPago", mediosPagoActivos); // Solo los activos
        return "caja/caja-carrito";
    }

 
}