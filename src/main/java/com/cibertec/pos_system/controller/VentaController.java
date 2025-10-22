package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.ClienteEntity;
import com.cibertec.pos_system.entity.VentaEntity;
import com.cibertec.pos_system.repository.ClienteRepository;
import com.cibertec.pos_system.repository.ProductoRepository;
import com.cibertec.pos_system.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @GetMapping("/nueva")
    public String mostrarFormularioVenta(Model model) {
        VentaEntity venta = new VentaEntity();
        venta.setCliente(new ClienteEntity());
        venta.setDetalles(new ArrayList<>());

        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());

        return "ventas/venta-form";
    }

    @PostMapping("/guardar")
    public String registrarVenta(@ModelAttribute VentaEntity venta) {
        ventaService.registrarVenta(venta);
        return "redirect:/ventas/listar";
    }

    @GetMapping("/listar")
    public String listarVentas(Model model) {
        List<VentaEntity> ventas = ventaService.listarTodas(); // O usar ventaRepository directamente
        model.addAttribute("ventas", ventas);
        return "ventas/ventas-lista";
}
    @GetMapping("/preciosDescuentos")
    public String mostrarVistaPreciosDescuentos(Model model) {
        VentaEntity venta = new VentaEntity();
        venta.setCliente(new ClienteEntity());
        venta.setDetalles(new ArrayList<>());

        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());

        return "ventas/preciosDescuentos";
    }
}