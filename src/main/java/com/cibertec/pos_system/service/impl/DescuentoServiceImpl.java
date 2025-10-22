package com.cibertec.pos_system.service.impl;

import com.cibertec.pos_system.entity.DescuentoEntity;
import com.cibertec.pos_system.entity.ProductoEntity;
import com.cibertec.pos_system.repository.DescuentoRepository;
import com.cibertec.pos_system.service.DescuentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DescuentoServiceImpl implements DescuentoService {

    private final DescuentoRepository descuentoRepository;

    @Override
    public List<DescuentoEntity> listarActivos() {
        return descuentoRepository.findByActivoTrue();
    }

    @Override
    public List<DescuentoEntity> listarHistorial() {
        return descuentoRepository.findAll();
    }

    @Override
    public DescuentoEntity guardar(DescuentoEntity descuento) {
        return descuentoRepository.save(descuento);
    }

    @Override
    public void desactivar(Long id) {
        descuentoRepository.findById(id).ifPresent(descuento -> {
            descuento.setActivo(false);
            descuentoRepository.save(descuento);
        });
    }

    @Override
    public DescuentoEntity obtenerDescuentoAplicable(ProductoEntity producto) {
        LocalDate hoy = LocalDate.now();
        return descuentoRepository.findAll().stream()
                .filter(d -> d.isActivo() &&
                        (hoy.isEqual(d.getFechaInicio()) || hoy.isAfter(d.getFechaInicio())) &&
                        (hoy.isEqual(d.getFechaFin()) || hoy.isBefore(d.getFechaFin())))
                .filter(d ->
                        (d.getProducto() != null && d.getProducto().getId().equals(producto.getId())) ||
                        (d.getCategoria() != null && d.getCategoria().getId().equals(producto.getCategoria().getId()))
                )
                .findFirst()
                .orElse(null);
    }

    @Override
    public BigDecimal calcularPrecioConDescuento(ProductoEntity producto) {
        DescuentoEntity d = obtenerDescuentoAplicable(producto);
        if (d == null) return producto.getPrecio();

        BigDecimal precio = producto.getPrecio();
        BigDecimal valor = d.getValor();

        switch (d.getTipo()) {
            case PORCENTAJE:
                // precio - (precio * valor / 100)
                return precio.subtract(
                        precio.multiply(valor)
                              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                );

            case FIJO:
                // precio - valor
                return precio.subtract(valor).max(BigDecimal.ZERO);

            case DOS_POR_UNO:
                // precio / 2
                return precio.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

            default:
                return precio;
        }
    }
}