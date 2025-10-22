package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.CategoriaEntity;
import com.cibertec.pos_system.entity.DescuentoEntity;
import com.cibertec.pos_system.entity.ProductoEntity;
import com.cibertec.pos_system.enums.TipoDescuento;
import com.cibertec.pos_system.service.DescuentoService;
import com.cibertec.pos_system.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/descuentos")
public class DescuentoController {

    @Autowired
    private DescuentoService descuentoService;

    @Autowired
    private ProductoService productoService;

    

    @GetMapping("/activos")
    public String listarActivos(Model model) {
        model.addAttribute("descuentos", descuentoService.listarActivos());
        return "descuentos/activos";
    }
@GetMapping
public String redirigirAActivos() {
    return "redirect:/descuentos/activos";
}

    @GetMapping("/historial")
    public String listarHistorial(Model model) {
        model.addAttribute("descuentos", descuentoService.listarHistorial());
        return "descuentos/historial";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("descuento", new DescuentoEntity());
        model.addAttribute("tipos", List.of(TipoDescuento.PORCENTAJE, TipoDescuento.FIJO, TipoDescuento.DOS_POR_UNO));
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("categorias", productoService.listarCategorias());
        return "descuentos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarDescuento(@ModelAttribute("descuento") DescuentoEntity descuento,
                                   @RequestParam(value = "productoId", required = false) Long productoId,
                                   @RequestParam(value = "categoriaId", required = false) Long categoriaId) {

        // Producto asociado (solo si se seleccionó)
        if (productoId != null) {
            ProductoEntity producto = productoService.buscarPorId(productoId);
            if (producto != null) {
                descuento.setProducto(producto);
            }
        }

        // Categoría asociada (solo si se seleccionó)
        if (categoriaId != null) {
            CategoriaEntity categoria = productoService.buscarCategoriaPorId(categoriaId);
            if (categoria != null) {
                descuento.setCategoria(categoria);
            }
        }

        descuentoService.guardar(descuento);
        return "redirect:/descuentos/activos";
    }

    @GetMapping("/aplicados")
public String verPreciosConDescuento(Model model) {
    List<ProductoEntity> productos = productoService.listarSoloConDescuento();
    model.addAttribute("productos", productos);
    return "descuentos/precio-descuento";
}
}
