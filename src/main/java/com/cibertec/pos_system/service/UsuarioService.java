package com.cibertec.pos_system.service;

import com.cibertec.pos_system.entity.UsuarioEntity;
import com.cibertec.pos_system.repository.UsuarioRepository;
import com.cibertec.pos_system.service.impl.UsuarioServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UsuarioServiceInterface {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<UsuarioEntity> obtenerTodas() {
        return usuarioRepository.findAll();
    }

    @Override
    public UsuarioEntity obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);// si no encuentra devolvemos nulos
    }
    @Override
    public UserDetails obtenerPorUsuario(String username) throws UsernameNotFoundException {
        UsuarioEntity user = usuarioRepository.getUserByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return new MyUserDetails(user);
    }
    @Override
    public UsuarioEntity crearUser(UsuarioEntity user) {
        return usuarioRepository.save(user);
    }

    @Override
    public UsuarioEntity actualizarUser(Long id, UsuarioEntity user) {

        UsuarioEntity usuarioEncontrado = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // si no encuentra mandamos una excepcion

        // actualizamos valores
        usuarioEncontrado.setUsername(user.getUsername());
        usuarioEncontrado.setEnabled(user.isEnabled());

        // Actualizar roles
        if (user.getRoles() != null) {
            usuarioEncontrado.setRoles(user.getRoles());
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // Utilizamos BCryptPasswordEncoder para encriptar el password
            String encodedPassword = encoder.encode(user.getPassword()); // Encriptamos passworrd
            usuarioEncontrado.setPassword(encodedPassword); // actualizamos el password encriptado
        }

        return usuarioRepository.save(usuarioEncontrado); // guardamos
    }

    @Override
    public void eliminarUser(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public long contarUsers() {
        return usuarioRepository.count();
    }

    @Override
    public Optional<UsuarioEntity> obtener(Long id) {
        return usuarioRepository.findById(id);
    }

    // Método agregado para buscar usuario por username
    public UsuarioEntity obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // Agrega este método al final de tu UsuarioService
    public boolean esCajero(UsuarioEntity usuario) {
    return usuario.getRoles().stream()
            .anyMatch(rol -> "CASHIER".equals(rol.getNombre()));
}
}