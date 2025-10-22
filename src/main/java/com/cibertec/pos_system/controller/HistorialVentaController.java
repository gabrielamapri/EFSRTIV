package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.service.CajaVentaService;
import com.cibertec.pos_system.entity.CajaVentaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/historial")
public class HistorialVentaController {

    private final CajaVentaService cajaVentaService;

    public HistorialVentaController(CajaVentaService cajaVentaService) {
        this.cajaVentaService = cajaVentaService;
    }

    // Buscar historial de ventas por DNI con paginación
    @GetMapping("/cliente/{dni}")
    public String verHistorial(@PathVariable("dni") String dni, 
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        List<CajaVentaEntity> todasLasVentas = cajaVentaService.listarVentasPorClienteDni(dni);
        
        // Implementar paginación manual
        int size = 10;
        int start = page * size;
        int end = Math.min(start + size, todasLasVentas.size());
        List<CajaVentaEntity> ventasPaginadas = todasLasVentas.subList(start, end);
        
        int totalPages = (int) Math.ceil((double) todasLasVentas.size() / size);
        
        model.addAttribute("ventas", ventasPaginadas);
        model.addAttribute("compras", ventasPaginadas); // Para compatibilidad con template
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("dni", dni);
        
        if (!todasLasVentas.isEmpty()) {
            CajaVentaEntity primeraVenta = todasLasVentas.get(0);
            if (primeraVenta.getCliente() != null) {
                model.addAttribute("nombre", primeraVenta.getCliente().getNombre() + " " + 
                                           primeraVenta.getCliente().getApellido());
            }
        }
        
        if (todasLasVentas.size() >= 10) {
            model.addAttribute("descuentoMensaje", "¡Felicidades! Tienes un 60% de descuento en tu próxima compra.");
        }
        return "cliente/historial";
    }

    // Buscar historial de ventas por ID
    @GetMapping("/cliente-id/{id}")
    public String verHistorialPorId(@PathVariable("id") Long clienteId, Model model) {
        List<CajaVentaEntity> ventas = cajaVentaService.listarVentasPorClienteId(clienteId);
        model.addAttribute("ventas", ventas);
        model.addAttribute("compras", ventas); // Para compatibilidad con template
        model.addAttribute("clienteId", clienteId);
        return "cliente/historial";
    }

    // Nueva ruta que detecta si es DNI o ID
    @GetMapping("/buscar")
    public String buscarCliente(@RequestParam("valor") String valor, Model model) {
        List<CajaVentaEntity> ventas;
        try {
            Long clienteId = Long.parseLong(valor);
            ventas = cajaVentaService.listarVentasPorClienteId(clienteId);
            model.addAttribute("clienteId", clienteId);
        } catch (NumberFormatException e) {
            ventas = cajaVentaService.listarVentasPorClienteDni(valor);
            model.addAttribute("dni", valor);
        }
        model.addAttribute("ventas", ventas);
        model.addAttribute("compras", ventas); // Para compatibilidad con template
        if (ventas.size() >= 10) {
            model.addAttribute("descuentoMensaje", "¡Felicidades! Tienes un 60% de descuento en tu próxima compra.");
        }
        return "cliente/historial";
    }
}
