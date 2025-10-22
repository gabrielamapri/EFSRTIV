package com.cibertec.pos_system.service.impl;

import com.cibertec.pos_system.entity.ClienteEntity;
import com.cibertec.pos_system.repository.ClienteRepository;
import com.cibertec.pos_system.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl extends ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        super(clienteRepository);
    }

    @Override
    public List<ClienteEntity> listar() {
        return clienteRepository.findAll();
    }

    @Override
    public ClienteEntity guardar(ClienteEntity cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public ClienteEntity actualizar(Long id, ClienteEntity cliente) {
        cliente.setId(id);
        return clienteRepository.save(cliente);
    }

    @Override
    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public Optional<ClienteEntity> obtener(Long id) {
        return clienteRepository.findById(id);
    }
}