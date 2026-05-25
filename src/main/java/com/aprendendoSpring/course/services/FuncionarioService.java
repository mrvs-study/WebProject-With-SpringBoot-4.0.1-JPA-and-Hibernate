package com.aprendendoSpring.course.services;

import com.aprendendoSpring.course.dtos.FuncionarioRequestDTO;
import com.aprendendoSpring.course.entities.Funcionario;
import com.aprendendoSpring.course.exceptions.BusinessException;
import com.aprendendoSpring.course.exceptions.ResourceNotFoundException;
import com.aprendendoSpring.course.repositories.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Funcionario> findAll() {
        return funcionarioRepository.findAll();
    }

    public Funcionario findById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Informe um ID de funcionário válido.");
        }
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado. ID: " + id));
    }

    public Optional<Funcionario> autenticar(String email, String senha) {
        return funcionarioRepository.findByEmail(email)
                .filter(f -> f.getSenha().equals(senha));
    }

    public Funcionario insert(FuncionarioRequestDTO dto) {
        validarFuncionario(dto);
        if (funcionarioRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("Já existe funcionário cadastrado com este CPF.");
        }
        if (funcionarioRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Já existe funcionário cadastrado com este e-mail.");
        }
        Funcionario funcionario = new Funcionario(null, dto.nome(), dto.cpf(), dto.email(), dto.telefone(), dto.senha(), dto.cargo(), dto.salario(), normalizarData(dto.dataDeAdmissao()));
        return funcionarioRepository.save(funcionario);
    }

    public Funcionario update(Long id, FuncionarioRequestDTO dto) {
        Funcionario funcionario = findById(id);
        validarFuncionarioUpdate(dto);
        funcionario.setNome(dto.nome());
        funcionario.setCpf(dto.cpf());
        funcionario.setEmail(dto.email());
        funcionario.setTelefone(dto.telefone());
        funcionario.setCargo(dto.cargo());
        funcionario.setSalario(dto.salario());
        if (dto.senha() != null && !dto.senha().isBlank()) {
            if (dto.senha().length() < 4) {
                throw new BusinessException("A senha deve ter pelo menos 4 caracteres.");
            }
            funcionario.setSenha(dto.senha());
        }
        funcionario.setDataDeAdmissao(normalizarData(dto.dataDeAdmissao()));
        funcionario.alterarCadastro();
        return funcionarioRepository.save(funcionario);
    }

    public void delete(Long id) {
        Funcionario funcionario = findById(id);
        funcionarioRepository.delete(funcionario);
    }

    private Instant normalizarData(Instant dataDeAdmissao) {
        return dataDeAdmissao == null ? Instant.now() : dataDeAdmissao;
    }

    private void validarFuncionario(FuncionarioRequestDTO dto) {
        validarFuncionarioUpdate(dto);
        if (dto.senha() == null || dto.senha().length() < 4) {
            throw new BusinessException("A senha do funcionário deve ter pelo menos 4 caracteres.");
        }
    }

    private void validarFuncionarioUpdate(FuncionarioRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Informe os dados do funcionário.");
        }
        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new BusinessException("O nome do funcionário é obrigatório.");
        }
        if (dto.cpf() == null || dto.cpf().isBlank()) {
            throw new BusinessException("O CPF do funcionário é obrigatório.");
        }
        if (dto.email() == null || dto.email().isBlank()) {
            throw new BusinessException("O e-mail do funcionário é obrigatório.");
        }
        if (dto.cargo() == null) {
            throw new BusinessException("O cargo do funcionário é obrigatório.");
        }
        if (dto.salario() == null || dto.salario() <= 0) {
            throw new BusinessException("O salário do funcionário deve ser maior que zero.");
        }
    }
}
