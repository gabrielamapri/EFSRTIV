package com.cibertec.pos_system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cibertec.pos_system.entity.UsuarioEntity;
import com.cibertec.pos_system.repository.UsuarioRepository;
import com.cibertec.pos_system.service.MyUserDetails;

public class UsuarioDetalleServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioEntity user = userRepository.getUserByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return new MyUserDetails(user);
    }
}
