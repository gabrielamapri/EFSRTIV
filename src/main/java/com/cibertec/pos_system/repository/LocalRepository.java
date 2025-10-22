package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.LocalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalRepository extends JpaRepository<LocalEntity, Long> {
}