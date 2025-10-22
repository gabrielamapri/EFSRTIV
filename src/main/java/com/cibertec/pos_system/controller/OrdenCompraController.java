package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.dto.ProductoDTO;
import com.cibertec.pos_system.entity.OrdenCompraDetalleEntity;
import com.cibertec.pos_system.entity.OrdenCompraEntity;
import com.cibertec.pos_system.entity.ProductoEntity;
import com.cibertec.pos_system.entity.ProveedorEntity;
import com.cibertec.pos_system.entity.UsuarioEntity;
import com.cibertec.pos_system.repository.UsuarioRepository;
import com.cibertec.pos_system.service.OrdenCompraService;
import com.cibertec.pos_system.service.ProductoService;
import com.cibertec.pos_system.service.ProveedorService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/compras")
@RequiredArgsConstructor
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;
    private final ProductoService productoService;
    private final ProveedorService proveedorService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/ordenes")
    public String listar(Model model) {
        model.addAttribute("ordenes", ordenCompraService.listarOrdenes());
        return "orden/listado";
    }

    @GetMapping("/orden/nueva")
    public String nuevaOrden(Model model) {
        model.addAttribute("orden", new OrdenCompraEntity());
        model.addAttribute("proveedores", proveedorService.listar());
        return "orden/formulario";
    }

    @PostMapping("/orden")
    public String guardarOrden(
        @RequestParam String ruc,
        HttpServletRequest request) {

        Long usuarioId = obtenerUsuarioId();
        ProveedorEntity proveedor = proveedorService.obtenerProveedorPorRuc(ruc);
        if (proveedor == null) {
            throw new IllegalArgumentException("Proveedor no encontrado para el RUC: " + ruc);
        }

        List<OrdenCompraDetalleEntity> detalles = new ArrayList<>();
        int i = 0;
        while (true) {
            String productoIdStr = request.getParameter("detalles[" + i + "].producto.id");
            String cantidadStr = request.getParameter("detalles[" + i + "].cantidad");
            if (productoIdStr == null || cantidadStr == null) break;

            // Solo agregar si cantidad es válida y mayor a 0
            if (cantidadStr != null && !cantidadStr.isBlank()) {
                try {
                    int cantidad = Integer.parseInt(cantidadStr);
                    if (cantidad > 0) {
                        OrdenCompraDetalleEntity detalle = new OrdenCompraDetalleEntity();
                        ProductoEntity producto = new ProductoEntity();
                        producto.setId(Long.parseLong(productoIdStr));
                        detalle.setProducto(producto);
                        detalle.setCantidad(cantidad);
                        detalles.add(detalle);
                    }
                } catch (NumberFormatException e) {
                    // Ignorar cantidades no numéricas
                }
            }
            i++;
        }

        ordenCompraService.crearOrden(proveedor.getId(), usuarioId, detalles);
        return "redirect:/compras/ordenes";
    }

    public Long obtenerUsuarioId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        
        UsuarioEntity usuario = usuarioRepository.findByUsername(username);
    
        if (usuario != null) {
            return usuario.getId();
        } else {
            throw new IllegalStateException("Usuario no encontrado para el nombre de usuario: " + username);
        }
    }
    
    @GetMapping("/productos")
    @ResponseBody
    public List<ProductoDTO> obtenerProductosPorProveedor(@RequestParam String ruc) {
        ProveedorEntity proveedor = proveedorService.obtenerProveedorPorRuc(ruc);
        if (proveedor == null) {
            throw new IllegalArgumentException("Proveedor no encontrado para el RUC: " + ruc);
        }

        List<ProductoEntity> productos = productoService.obtenerProductosPorProveedor(proveedor.getId());

        return productos.stream().map(p -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setId(p.getId());
            dto.setNombre(p.getNombre());
            dto.setPrecio(p.getPrecio());
            dto.setStockActual(p.getStockActual());
            return dto;
        }).toList();
    }

    @GetMapping("/orden/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        model.addAttribute("orden", ordenCompraService.obtenerPorId(id));
        return "orden/detalle";
    }

    @PostMapping("/orden/{id}/recibir")
    public String marcarRecibida(@PathVariable Long id) {
        ordenCompraService.marcarComoRecibida(id);
        return "redirect:/compras/ordenes";
    }

    @PostMapping("/orden/{id}/cancelar")
    public String cancelarOrden(@PathVariable Long id) {
        ordenCompraService.cancelarOrden(id);
        return "redirect:/compras/ordenes";
    }
}
