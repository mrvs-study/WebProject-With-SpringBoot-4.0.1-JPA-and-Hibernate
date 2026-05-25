package com.aprendendoSpring.course.services;

import com.aprendendoSpring.course.dtos.ProdutoRequestDTO;
import com.aprendendoSpring.course.entities.Produto;
import com.aprendendoSpring.course.exceptions.BusinessException;
import com.aprendendoSpring.course.exceptions.ResourceNotFoundException;
import com.aprendendoSpring.course.repositories.ProdutoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public Produto findById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. ID: " + id));
    }

    public Produto insert(ProdutoRequestDTO dto) {
        validarProduto(dto);

        Produto produto = new Produto();
        produto.setNome(dto.nome());
        produto.setCategoria(dto.categoria());
        produto.setPreco(dto.preco());
        produto.setDataDeValidade(dto.dataDeValidade());
        produto.setImgUrlProduto(dto.imgUrlProduto());

        return produtoRepository.save(produto);
    }

    public Produto update(Long id, ProdutoRequestDTO dto) {
        Produto produto = findById(id);

        if (dto.nome() != null) {
            produto.setNome(dto.nome());
        }

        if (dto.categoria() != null) {
            produto.setCategoria(dto.categoria());
        }

        if (dto.preco() != null) {
            if (dto.preco() <= 0) {
                throw new BusinessException("O preço do produto deve ser maior que zero.");
            }
            produto.setPreco(dto.preco());
        }

        if (dto.dataDeValidade() != null) {
            produto.setDataDeValidade(dto.dataDeValidade());
        }

        if (dto.imgUrlProduto() != null) {
            produto.setImgUrlProduto(dto.imgUrlProduto());
        }

        return produtoRepository.save(produto);
    }

    public void delete(Long id) {
        Produto produto = findById(id);
        produtoRepository.delete(produto);
    }

    private void validarProduto(ProdutoRequestDTO dto) {
        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new BusinessException("O nome do produto é obrigatório.");
        }

        if (dto.categoria() == null || dto.categoria().isBlank()) {
            throw new BusinessException("A categoria do produto é obrigatória.");
        }

        if (dto.preco() == null || dto.preco() <= 0) {
            throw new BusinessException("O preço do produto deve ser maior que zero.");
        }
    }
}