package com.aprendendoSpring.course.services;

import com.aprendendoSpring.course.dtos.ClienteRequestDTO;
import com.aprendendoSpring.course.entities.Cliente;
import com.aprendendoSpring.course.exceptions.BusinessException;
import com.aprendendoSpring.course.exceptions.ResourceNotFoundException;
import com.aprendendoSpring.course.repositories.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente findById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Informe um ID de cliente válido.");
        }
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado. ID: " + id));
    }

    public Cliente insert(ClienteRequestDTO dto) {
        validarCliente(dto);
        if (clienteRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("Já existe cliente cadastrado com este CPF.");
        }
        if (clienteRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Já existe cliente cadastrado com este e-mail.");
        }
        Cliente cliente = new Cliente(null, dto.nome(), dto.cpf(), dto.email(), dto.telefone(), dto.senha());
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, ClienteRequestDTO dto) {
        Cliente cliente = findById(id);
        validarClienteUpdate(dto);
        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        if (dto.senha() != null && !dto.senha().isBlank()) {
            if (dto.senha().length() < 4) {
                throw new BusinessException("A senha deve ter pelo menos 4 caracteres.");
            }
            cliente.setSenha(dto.senha());
        }
        cliente.atualizarCadastro();
        return clienteRepository.save(cliente);
    }

    public void delete(Long id) {
        Cliente cliente = findById(id);
        clienteRepository.delete(cliente);
    }

    private void validarCliente(ClienteRequestDTO dto) {
        validarClienteUpdate(dto);
        if (dto.senha() == null || dto.senha().length() < 4) {
            throw new BusinessException("A senha do cliente deve ter pelo menos 4 caracteres.");
        }
    }

    private void validarClienteUpdate(ClienteRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Informe os dados do cliente.");
        }
        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new BusinessException("O nome do cliente é obrigatório.");
        }
        if (dto.cpf() == null || dto.cpf().isBlank()) {
            throw new BusinessException("O CPF do cliente é obrigatório.");
        }
        if (dto.email() == null || dto.email().isBlank()) {
            throw new BusinessException("O e-mail do cliente é obrigatório.");
        }
    }
}
