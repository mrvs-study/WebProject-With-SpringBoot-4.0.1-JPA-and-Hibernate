package com.aprendendoSpring.course.services;

import com.aprendendoSpring.course.dtos.FornecedorRequestDTO;
import com.aprendendoSpring.course.entities.Fornecedor;
import com.aprendendoSpring.course.exceptions.BusinessException;
import com.aprendendoSpring.course.exceptions.ResourceNotFoundException;
import com.aprendendoSpring.course.repositories.FornecedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    public List<Fornecedor> findAll() {
        return fornecedorRepository.findAll();
    }

    public Fornecedor findById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Informe um ID de fornecedor válido.");
        }
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado. ID: " + id));
    }

    public Fornecedor insert(FornecedorRequestDTO dto) {
        validarFornecedor(dto);
        if (fornecedorRepository.existsByCnpj(dto.cnpj())) {
            throw new BusinessException("Já existe fornecedor cadastrado com este CNPJ.");
        }
        if (fornecedorRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Já existe fornecedor cadastrado com este e-mail.");
        }
        Fornecedor fornecedor = new Fornecedor(null, dto.razaoSocial(), dto.cnpj(), dto.telefone(), dto.email(), dto.cep());
        fornecedor.fornecerProduto();
        return fornecedorRepository.save(fornecedor);
    }

    public Fornecedor update(Long id, FornecedorRequestDTO dto) {
        Fornecedor fornecedor = findById(id);
        validarFornecedor(dto);
        fornecedor.setRazaoSocial(dto.razaoSocial());
        fornecedor.setCnpj(dto.cnpj());
        fornecedor.setTelefone(dto.telefone());
        fornecedor.setEmail(dto.email());
        fornecedor.setCep(dto.cep());
        return fornecedorRepository.save(fornecedor);
    }

    public void delete(Long id) {
        Fornecedor fornecedor = findById(id);
        fornecedorRepository.delete(fornecedor);
    }

    private void validarFornecedor(FornecedorRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Informe os dados do fornecedor.");
        }
        if (dto.razaoSocial() == null || dto.razaoSocial().isBlank()) {
            throw new BusinessException("A razão social é obrigatória.");
        }
        if (dto.cnpj() == null || dto.cnpj().isBlank()) {
            throw new BusinessException("O CNPJ é obrigatório.");
        }
        if (dto.email() == null || dto.email().isBlank()) {
            throw new BusinessException("O e-mail do fornecedor é obrigatório.");
        }
    }
}
