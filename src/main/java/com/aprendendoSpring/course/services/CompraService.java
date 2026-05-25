package com.aprendendoSpring.course.services;

import com.aprendendoSpring.course.dtos.CompraRequestDTO;
import com.aprendendoSpring.course.dtos.ItemCompraRequestDTO;
import com.aprendendoSpring.course.entities.Compra;
import com.aprendendoSpring.course.entities.Fornecedor;
import com.aprendendoSpring.course.entities.ItemCompra;
import com.aprendendoSpring.course.entities.Produto;
import com.aprendendoSpring.course.entities.enums.StatusCompra;
import com.aprendendoSpring.course.exceptions.BusinessException;
import com.aprendendoSpring.course.exceptions.ResourceNotFoundException;
import com.aprendendoSpring.course.repositories.CompraRepository;
import com.aprendendoSpring.course.repositories.FornecedorRepository;
import com.aprendendoSpring.course.repositories.ProdutoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ProdutoRepository produtoRepository;

    public CompraService(
            CompraRepository compraRepository,
            FornecedorRepository fornecedorRepository,
            ProdutoRepository produtoRepository) {

        this.compraRepository = compraRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<Compra> findAll() {
        return compraRepository.findAll();
    }

    public Compra findById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Informe um ID de compra válido.");
        }

        return compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada. ID: " + id));
    }

    public Compra insert(CompraRequestDTO dto) {
        validarCompra(dto);

        Fornecedor fornecedor = fornecedorRepository.findById(dto.fornecedorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fornecedor não encontrado. ID: " + dto.fornecedorId()
                ));

        Compra compra = new Compra();
        compra.setFornecedor(fornecedor);

        for (ItemCompraRequestDTO itemDto : dto.itens()) {
            Produto produto = produtoRepository.findById(itemDto.produtoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produto não encontrado. ID: " + itemDto.produtoId()
                    ));

            ItemCompra item = new ItemCompra(produto, compra, itemDto.quantidade());
            compra.getItens().add(item);
        }

        compra.registrarCompra();

        return compraRepository.save(compra);
    }

    public Compra finalizarCompra(Long id) {
        Compra compra = findById(id);

        if (compra.getStatus() == StatusCompra.RECEBIDA) {
            throw new BusinessException("A compra já está recebida.");
        }

        compra.calcularTotal();
        compra.atualizarStatus();

        return compraRepository.save(compra);
    }

    public List<Compra> buscarComprasPorFornecedor(Long fornecedorId) {
        if (fornecedorId == null || fornecedorId <= 0) {
            throw new BusinessException("Informe um fornecedor válido.");
        }

        return compraRepository.buscarComprasPorFornecedor(fornecedorId);
    }

    private void validarCompra(CompraRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Informe os dados da compra.");
        }

        if (dto.fornecedorId() == null || dto.fornecedorId() <= 0) {
            throw new BusinessException("Informe um fornecedor válido para a compra.");
        }

        if (dto.itens() == null || dto.itens().isEmpty()) {
            throw new BusinessException("A compra deve ter pelo menos um item.");
        }

        for (ItemCompraRequestDTO item : dto.itens()) {
            if (item.produtoId() == null || item.produtoId() <= 0) {
                throw new BusinessException("Todos os itens da compra devem informar um produto válido.");
            }

            if (item.quantidade() == null || item.quantidade() <= 0) {
                throw new BusinessException("A quantidade de cada item da compra deve ser maior que zero.");
            }
        }
    }
}