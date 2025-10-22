package com.cibertec.pos_system.service;

import com.cibertec.pos_system.entity.ClienteEntity;
import com.cibertec.pos_system.entity.VentaDetalleEntity;
import com.cibertec.pos_system.entity.VentaEntity;
import com.cibertec.pos_system.repository.ClienteRepository;
import com.cibertec.pos_system.repository.VentaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public VentaEntity registrarVenta(VentaEntity venta) {
        venta.setFecha(LocalDateTime.now());

        VentaEntity ventaGuardada = ventaRepository.save(venta);

        if (venta.isRecibePuntos()) {
            asignarPuntos(ventaGuardada.getCliente(), ventaGuardada.getDetalles());
        }

        return ventaGuardada;
    }

    private void asignarPuntos(ClienteEntity cliente, List<VentaDetalleEntity> detalles) {
        int puntos = calcularPuntosPorTipoCliente(cliente.getTipo(), detalles);
        int actuales = cliente.getPuntosAcumulados() != null ? cliente.getPuntosAcumulados() : 0;
        cliente.setPuntosAcumulados(actuales + puntos);
        clienteRepository.save(cliente);
    }

    private int calcularPuntosPorTipoCliente(String tipo, List<VentaDetalleEntity> detalles) {
        int totalCantidad = detalles.stream().mapToInt(VentaDetalleEntity::getCantidad).sum();

        return switch (tipo.toLowerCase()) {
            case "vip" -> totalCantidad * 2;
            case "regular" -> totalCantidad;
            default -> 0;
        };
    }

    public List<VentaEntity> listarTodas() {
        return ventaRepository.findAll();
}
}