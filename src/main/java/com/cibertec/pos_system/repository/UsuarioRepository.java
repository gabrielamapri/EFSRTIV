package com.cibertec.pos_system.repository;

import com.cibertec.pos_system.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    // Esta es una consulta personalizada que busca un usuario por su nombre de usuario
    @Query("SELECT u FROM UsuarioEntity u WHERE u.username = :username")
    // Este método retorna un usuario que tenga ese username
    UsuarioEntity getUserByUsername(@Param("username") String username);
    //Se usa cuando queremos buscar un usuario (login)

    UsuarioEntity findByUsername(String username);

}