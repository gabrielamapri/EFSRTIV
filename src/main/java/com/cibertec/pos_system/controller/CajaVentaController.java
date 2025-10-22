package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.dto.CajaVentaDTO;
import com.cibertec.pos_system.dto.CajaVentaDetalleDTO;
import com.cibertec.pos_system.entity.*;
import com.cibertec.pos_system.service.*;
import com.cibertec.pos_system.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/caja-venta")
public class CajaVentaController {

    @Autowired
    private CajaVentaService cajaVentaService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private ProductoService productoService;
    @Autowired
    private MedioPagoService medioPagoService;
    @Autowired
    private CajaSesionService cajaSesionService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DescuentoService descuentoService;
    
    @GetMapping
    public String listarVentas(Model model) {
        // Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        UsuarioEntity usuarioSesion = usuarioRepository.getUserByUsername(usuarioActual);

        // Verificar si es ADMIN
        boolean esAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        List<CajaVentaEntity> ventas = cajaVentaService.listar();
        model.addAttribute("listaVentas", ventas);
        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("usuarioActualId", usuarioSesion.getId());
        
        return "caja/caja-ventas-listar";
    }

    @PostMapping("/guardar")
    public String guardarVenta(@ModelAttribute CajaVentaDTO ventaDTO, RedirectAttributes redirectAttributes) {
        // Buscar entidades relacionadas y validar
        CajaSesionEntity cajaSesion = cajaSesionService.obtenerPorId(ventaDTO.getCajaSesionId());
        if (cajaSesion == null) throw new RuntimeException("Sesión de caja no encontrada");

        ClienteEntity cliente = ventaDTO.getClienteId() != null ? clienteService.obtener(ventaDTO.getClienteId()).orElse(null) : null;

        MedioPagoEntity medioPago = medioPagoService.obtener(ventaDTO.getMedioPagoId()).orElse(null);
        if (medioPago == null) throw new RuntimeException("Medio de pago no encontrado");

        // Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UsuarioEntity usuario = usuarioService.obtenerPorUsername(username);
        if (usuario == null) throw new RuntimeException("Usuario no encontrado");

        // Crear la venta (tu lógica actual)
        CajaVentaEntity venta = new CajaVentaEntity();
    venta.setCajaSesion(cajaSesion);
    venta.setCliente(cliente);
    venta.setMedioPago(medioPago);
    venta.setUsuario(usuario);
    venta.setFechaHora(LocalDateTime.now());
    venta.setSubtotal(ventaDTO.getSubtotal());
    venta.setDescuento(ventaDTO.getDescuento());  // ✅ AGREGAR ESTA LÍNEA
    venta.setImpuesto(ventaDTO.getImpuesto());
    venta.setTotal(ventaDTO.getTotal());
    venta.setTipoComprobante(ventaDTO.getTipoComprobante());
    venta.setEstado("FINALIZADA");
    

        // Crear detalles
        List<CajaVentaDetalleEntity> detalles = new ArrayList<>();
        if (ventaDTO.getDetalles() != null) {
            for (CajaVentaDetalleDTO detDTO : ventaDTO.getDetalles()) {
                ProductoEntity producto = productoService.obtener(detDTO.getProductoId()).orElse(null);
                if (producto == null) throw new RuntimeException("Producto no encontrado");
                CajaVentaDetalleEntity detalle = new CajaVentaDetalleEntity();
                detalle.setVenta(venta);
                detalle.setProducto(producto);
                detalle.setCantidad(detDTO.getCantidad());
                detalle.setPrecioUnitario(producto.getPrecio());
                detalles.add(detalle);
            }
        }
        venta.setDetalles(detalles);

        // --- AGREGADO: también registrar la venta usando el método que genera el número de comprobante ---
        Long idVenta = cajaVentaService.registrarVenta(ventaDTO, username);

        redirectAttributes.addFlashAttribute("exito", "¡Venta registrada correctamente!");
        // Redirigir directamente al comprobante de la venta recién creada
        return "redirect:/caja-venta/detalle/" + idVenta;
    }

    @GetMapping("/anular/{id}")
    public String mostrarFormularioAnular(@PathVariable Long id, Model model) {
        CajaVentaEntity venta = cajaVentaService.obtenerPorId(id);
        model.addAttribute("venta", venta);
        model.addAttribute("accion", "/caja-venta/anular/" + id);
        return "caja/caja-ventas-anular";
    }

    @PostMapping("/anular/{id}")
    public String anularVenta(@PathVariable Long id, @RequestParam String motivo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        cajaVentaService.anularVenta(id, motivo, username);
        return "redirect:/caja-venta";
    }

    @GetMapping("/detalle/{id}")
    public String detalleVenta(@PathVariable Long id, Model model) {
        CajaVentaEntity venta = cajaVentaService.obtenerPorId(id);
        model.addAttribute("venta", venta);
        return "caja/caja-venta-detalle";
    }

    @GetMapping("/api/caja/precio-descuento")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> calcularPrecioConDescuento(
            @RequestParam Long productoId,
            @RequestParam int cantidad,
            @RequestParam BigDecimal precio) {
        
        try {
            BigDecimal precioConDescuento = cajaVentaService.calcularPrecioConDescuento(productoId, cantidad, precio);
            BigDecimal precioOriginal = precio.multiply(BigDecimal.valueOf(cantidad));
            BigDecimal descuentoAplicado = precioOriginal.subtract(precioConDescuento);
            
            Map<String, Object> response = new HashMap<>();
            response.put("precioOriginal", precioOriginal);
            response.put("precioConDescuento", precioConDescuento);
            response.put("descuentoAplicado", descuentoAplicado);
            response.put("tieneDescuento", descuentoAplicado.compareTo(BigDecimal.ZERO) > 0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al calcular descuento: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // PASO 7: AGREGAR este nuevo endpoint
    @GetMapping("/api/caja/venta/{id}/descuentos")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerDescuentosVenta(@PathVariable Long id) {
        try {
            CajaVentaEntity venta = cajaVentaService.obtenerPorId(id);
            if (venta == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("ventaId", venta.getId());
            response.put("numeroComprobante", venta.getNumeroComprobante());
            response.put("subtotalSinDescuento", venta.getSubtotalSinDescuento());
            response.put("totalDescuentos", venta.getTotalDescuentos());
            response.put("total", venta.getTotal());
            
            List<Map<String, Object>> detalles = new ArrayList<>();
            for (CajaVentaDetalleEntity det : venta.getDetalles()) {
                Map<String, Object> detalle = new HashMap<>();
                detalle.put("producto", det.getProducto().getNombre());
                detalle.put("cantidad", det.getCantidad());
                detalle.put("precioOriginal", det.getPrecioOriginal());
                detalle.put("descuentoAplicado", det.getDescuentoAplicado());
                detalle.put("tipoDescuento", det.getTipoDescuento());
                detalle.put("subtotal", det.getSubtotal());
                detalles.add(detalle);
            }
            response.put("detalles", detalles);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener información de descuentos: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

     @GetMapping("/api/caja/producto/{id}/descuentos-disponibles")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerDescuentosDisponibles(@PathVariable Long id) {
        try {
            ProductoEntity producto = productoService.obtener(id).orElse(null);
            if (producto == null) {
                return ResponseEntity.notFound().build();
            }
            
            DescuentoEntity descuento = descuentoService.obtenerDescuentoAplicable(producto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("productoId", producto.getId());
            response.put("productoNombre", producto.getNombre());
            response.put("precioOriginal", producto.getPrecio());
            
            if (descuento != null) {
                Map<String, Object> descuentoInfo = new HashMap<>();
                descuentoInfo.put("id", descuento.getId());
                descuentoInfo.put("nombre", descuento.getNombre());
                descuentoInfo.put("tipo", descuento.getTipo().toString());
                descuentoInfo.put("valor", descuento.getValor());
                descuentoInfo.put("fechaInicio", descuento.getFechaInicio());
                descuentoInfo.put("fechaFin", descuento.getFechaFin());
                
                response.put("tieneDescuento", true);
                response.put("descuento", descuentoInfo);
                
                // Calcular precio con descuento para cantidad 1
                BigDecimal precioConDescuento = cajaVentaService.calcularPrecioConDescuento(id, 1, producto.getPrecio());
                response.put("precioConDescuento", precioConDescuento);
                response.put("ahorroUnitario", producto.getPrecio().subtract(precioConDescuento));
            } else {
                response.put("tieneDescuento", false);
                response.put("descuento", null);
                response.put("precioConDescuento", producto.getPrecio());
                response.put("ahorroUnitario", BigDecimal.ZERO);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener descuentos disponibles: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
       // --- NUEVOS ENDPOINTS: Historial de ventas por cliente ---

    @GetMapping("/historial/dni")
    public String verHistorialPorDni(@RequestParam("dni") String dni, Model model) {
        List<CajaVentaEntity> ventas = cajaVentaService.listarVentasPorClienteDni(dni);
        model.addAttribute("ventas", ventas);
        model.addAttribute("criterio", "DNI: " + dni);
        return "caja/ventas-historial";
    }

    @GetMapping("/historial/id")
    public String verHistorialPorId(@RequestParam("id") Long id, Model model) {
        List<CajaVentaEntity> ventas = cajaVentaService.listarVentasPorClienteId(id);
        model.addAttribute("ventas", ventas);
        model.addAttribute("criterio", "ID: " + id);
        return "caja/ventas-historial";
    }
}
