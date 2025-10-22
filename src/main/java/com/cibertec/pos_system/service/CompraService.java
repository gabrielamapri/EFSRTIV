package com.cibertec.pos_system.service;

import com.cibertec.pos_system.entity.CompraEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.cibertec.pos_system.entity.CompraEntity;

import java.util.List;

public interface CompraService {
    List<CompraEntity> listarComprasPorCliente(String dni);
    List<CompraEntity> listarComprasPorClienteId(Long clienteId);
    Page<CompraEntity> listarComprasPorClientePaginado(String dni, Pageable pageable);

    CompraEntity registrarCompra(CompraEntity compra);
    long contarComprasPorCliente(String dni);

    Page<CompraEntity> listarComprasPorClienteIdPaginado(Long clienteId, Pageable pageable);

    List<CompraEntity> listarTodasLasCompras();

    Page<CompraEntity> listarTodasLasComprasPaginadas(Pageable pageable);


}
