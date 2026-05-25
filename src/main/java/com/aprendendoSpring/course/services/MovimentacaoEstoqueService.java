package com.aprendendoSpring.course.services;

import com.aprendendoSpring.course.dtos.MovimentacaoEstoqueRequestDTO;
import com.aprendendoSpring.course.entities.Estoque;
import com.aprendendoSpring.course.entities.MovimentacaoEstoque;
import com.aprendendoSpring.course.entities.enums.TipoMovimentacao;
import com.aprendendoSpring.course.exceptions.BusinessException;
import com.aprendendoSpring.course.exceptions.ResourceNotFoundException;
import com.aprendendoSpring.course.repositories.EstoqueRepository;
import com.aprendendoSpring.course.repositories.MovimentacaoEstoqueRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final EstoqueRepository estoqueRepository;

    public MovimentacaoEstoqueService(
            MovimentacaoEstoqueRepository movimentacaoRepository,
            EstoqueRepository estoqueRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    public List<MovimentacaoEstoque> findAll() {
        return movimentacaoRepository.findAll();
    }

    public MovimentacaoEstoque registrarEntrada(MovimentacaoEstoqueRequestDTO dto) {
        return registrar(dto, TipoMovimentacao.ENTRADA);
    }

    public MovimentacaoEstoque registrarSaida(MovimentacaoEstoqueRequestDTO dto) {
        return registrar(dto, TipoMovimentacao.SAIDA);
    }

    public List<MovimentacaoEstoque> buscarPorTipoMovimentacao(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new BusinessException("Informe um tipo de movimentação válido.");
        }

        TipoMovimentacao tipoMovimentacao;

        try {
            tipoMovimentacao = TipoMovimentacao.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de movimentação inválido. Use ENTRADA ou SAIDA.");
        }

        return movimentacaoRepository.buscarPorTipoMovimentacao(tipoMovimentacao.name());
    }

    private MovimentacaoEstoque registrar(MovimentacaoEstoqueRequestDTO dto, TipoMovimentacao tipo) {
        validarMovimentacao(dto);

        Estoque estoque = estoqueRepository.findById(dto.estoqueId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estoque não encontrado. ID: " + dto.estoqueId()
                ));

        if (tipo == TipoMovimentacao.SAIDA && estoque.verificarEstoque() < dto.quantidade()) {
            throw new BusinessException("Estoque insuficiente para registrar saída.");
        }

        if (tipo == TipoMovimentacao.ENTRADA) {
            estoque.setQuantidadeAtual(estoque.verificarEstoque() + dto.quantidade());
        } else if (tipo == TipoMovimentacao.SAIDA) {
            estoque.setQuantidadeAtual(estoque.verificarEstoque() - dto.quantidade());
        }

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setEstoque(estoque);
        movimentacao.setQuantidade(dto.quantidade());
        movimentacao.setMotivo(dto.motivo());
        movimentacao.setTipoMovimentacao(tipo);

        if (tipo == TipoMovimentacao.ENTRADA) {
            movimentacao.registrarEntrada();
        } else if (tipo == TipoMovimentacao.SAIDA) {
            movimentacao.registrarSaida();
        }

        estoqueRepository.save(estoque);
        return movimentacaoRepository.save(movimentacao);
    }

    private void validarMovimentacao(MovimentacaoEstoqueRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Informe os dados da movimentação.");
        }

        if (dto.estoqueId() == null || dto.estoqueId() <= 0) {
            throw new BusinessException("Informe um estoque válido.");
        }

        if (dto.quantidade() == null || dto.quantidade() <= 0) {
            throw new BusinessException("A quantidade movimentada deve ser maior que zero.");
        }

        if (dto.motivo() == null || dto.motivo().isBlank()) {
            throw new BusinessException("O motivo da movimentação é obrigatório.");
        }
    }
}