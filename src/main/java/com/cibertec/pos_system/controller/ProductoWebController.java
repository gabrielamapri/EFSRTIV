package com.cibertec.pos_system.controller;

import com.cibertec.pos_system.entity.CategoriaEntity;
import com.cibertec.pos_system.entity.ProductoEntity;
import com.cibertec.pos_system.entity.ProveedorEntity;
import com.cibertec.pos_system.service.CategoriaService;
import com.cibertec.pos_system.service.ProductoService;
import com.cibertec.pos_system.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/producto")
public class ProductoWebController {

    private final ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    public ProductoWebController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listarProductos(@RequestParam(value = "q", required = false) String q, Model model) {
        if (q != null && !q.trim().isEmpty()) {
            model.addAttribute("productos", productoService.buscarPorNombreCodigoProveedor(q));
            model.addAttribute("q", q);
        } else {
            model.addAttribute("productos", productoService.listar());
        }
        return "producto/lista";
    }

    @GetMapping("/descuentos")
    public String listarConDescuento(Model model) {
        List<ProductoEntity> productosConDescuento = productoService.listarSoloConDescuento();
        model.addAttribute("productos", productosConDescuento);
        return "descuento/preciosConDescuento";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProducto(Model model) {
        ProductoEntity producto = new ProductoEntity();
        producto.setCategoria(new CategoriaEntity()); // evitar NullPointer
        producto.setProveedor(new ProveedorEntity()); // evitar NullPointer

        model.addAttribute("producto", producto);
        model.addAttribute("listaCategorias", categoriaService.listar());
        model.addAttribute("listaProveedores", proveedorService.listar());
        return "producto/formulario";
    }

    @GetMapping("/editar/{id}")
public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
    ProductoEntity producto = productoService.obtener(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    model.addAttribute("producto", producto);
    model.addAttribute("listaCategorias", categoriaService.listar());
    model.addAttribute("listaProveedores", proveedorService.listar());

    return "producto/formulario"; // Usa la misma vista del formulario
}

@PostMapping("/guardar")
public String guardarProducto(@ModelAttribute ProductoEntity producto) {
    productoService.crear(producto); 
    return "redirect:/producto";
}
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/producto";
    }
}
