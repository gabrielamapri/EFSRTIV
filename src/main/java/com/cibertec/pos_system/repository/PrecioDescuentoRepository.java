// 1. Repositorio
package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.PrecioDescuentoEntity;
import com.cibertec.pos_system.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;//natm

import java.util.List;//natm
public interface PrecioDescuentoRepository extends JpaRepository<PrecioDescuentoEntity, Long> {
    Optional<PrecioDescuentoEntity> findByProductoAndVigenteTrue(ProductoEntity producto);
//natm
 @Query("SELECT pd.producto FROM PrecioDescuentoEntity pd WHERE pd.vigente = true")
    List<ProductoEntity> findProductosConDescuentoVigente();
}