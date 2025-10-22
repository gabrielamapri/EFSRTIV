package com.cibertec.pos_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.pos_system.entity.OrdenCompraDetalleEntity;
import com.cibertec.pos_system.entity.OrdenCompraEntity;
import com.cibertec.pos_system.entity.ProductoEntity;
import com.cibertec.pos_system.entity.ProveedorEntity;
import com.cibertec.pos_system.entity.UsuarioEntity;
import com.cibertec.pos_system.repository.OrdenCompraRepository;
import com.cibertec.pos_system.repository.ProductoRepository;
import com.cibertec.pos_system.repository.ProveedorRepository;
import com.cibertec.pos_system.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepo;
    private final ProductoRepository productoRepo;
    private final ProveedorRepository proveedorRepo;
    private final UsuarioRepository usuarioRepo;

    @Transactional
    public OrdenCompraEntity crearOrden(Long proveedorId, Long usuarioId, List<OrdenCompraDetalleEntity> detalles) {
        ProveedorEntity proveedor = proveedorRepo.findById(proveedorId).orElseThrow();
        UsuarioEntity usuario = usuarioRepo.findById(usuarioId).orElseThrow();

        OrdenCompraEntity orden = new OrdenCompraEntity();
        orden.setProveedor(proveedor);
        orden.setUsuario(usuario);
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setEstado("pendiente");
        orden.setDetalles(detalles);

        BigDecimal total = BigDecimal.ZERO;
        for (OrdenCompraDetalleEntity item : detalles) {
            ProductoEntity prod = productoRepo.findById(item.getProducto().getId()).orElseThrow();
            item.setProducto(prod);
            item.setOrdenCompra(orden);
            item.setPrecioUnitario(prod.getPrecio());
            total = total.add(prod.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
        }

        orden.setTotal(total);
        return ordenCompraRepo.save(orden);
    }

    @Transactional
    public OrdenCompraEntity marcarComoRecibida(Long idOrden) {
        OrdenCompraEntity orden = ordenCompraRepo.findById(idOrden).orElseThrow();
        if (!"pendiente".equals(orden.getEstado())) throw new IllegalStateException("Ya fue procesada");

        for (OrdenCompraDetalleEntity item : orden.getDetalles()) {
            ProductoEntity prod = item.getProducto();
            prod.setStockActual(prod.getStockActual() + item.getCantidad());
            productoRepo.save(prod);
        }
        orden.setEstado("recibido");
        return ordenCompraRepo.save(orden);
    }

    @Transactional
    public void cancelarOrden(Long idOrden) {
        OrdenCompraEntity orden = ordenCompraRepo.findById(idOrden).orElseThrow();
        if (!"pendiente".equals(orden.getEstado())) throw new IllegalStateException("Solo se puede cancelar una orden pendiente");
        orden.setEstado("cancelado");
        ordenCompraRepo.save(orden);
    }

    public List<OrdenCompraEntity> listarOrdenes() {
        return ordenCompraRepo.findAll();
    }

    public OrdenCompraEntity obtenerPorId(Long id) {
        return ordenCompraRepo.findById(id).orElseThrow();
    }
}