package com.aprendendoSpring.course.services;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aprendendoSpring.course.dtos.EstoqueRequestDTO;
import com.aprendendoSpring.course.entities.Estoque;
import com.aprendendoSpring.course.entities.MovimentacaoEstoque;
import com.aprendendoSpring.course.entities.Produto;
import com.aprendendoSpring.course.entities.enums.TipoMovimentacao;
import com.aprendendoSpring.course.exceptions.BusinessException;
import com.aprendendoSpring.course.exceptions.ResourceNotFoundException;
import com.aprendendoSpring.course.repositories.EstoqueRepository;
import com.aprendendoSpring.course.repositories.MovimentacaoEstoqueRepository;
import com.aprendendoSpring.course.repositories.ProdutoRepository;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public EstoqueService(EstoqueRepository estoqueRepository, ProdutoRepository produtoRepository, MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<Estoque> findAll() {
        return estoqueRepository.findAll();
    }

    public Estoque findById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Informe um ID de estoque válido.");
        }
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado. ID: " + id));
    }

    public Estoque insert(EstoqueRequestDTO dto) {
        validarEstoque(dto);
        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. ID: " + dto.produtoId()));
        if (estoqueRepository.findByProduto_IdProduto(dto.produtoId()).isPresent()) {
            throw new BusinessException("Já existe estoque cadastrado para este produto.");
        }
        Estoque estoque = new Estoque(null, produto, dto.quantidadeAtual(), dto.quantidadeMaxima(), dto.localizacao());
        estoque.atualizarQuantidadeAtual();
        return estoqueRepository.save(estoque);
    }

    public Estoque update(Long id, EstoqueRequestDTO dto) {
        Estoque estoque = findById(id);
        validarEstoque(dto);
        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. ID: " + dto.produtoId()));
        estoque.setProduto(produto);
        estoque.setQuantidadeAtual(dto.quantidadeAtual());
        estoque.setQuantidadeMaxima(dto.quantidadeMaxima());
        estoque.setLocalizacao(dto.localizacao());
        estoque.atualizarQuantidadeAtual();
        return estoqueRepository.save(estoque);
    }

    public void delete(Long id) {
        Estoque estoque = findById(id);
        estoqueRepository.delete(estoque);
    }

    public Estoque adicionarQuantidade(Long id, int quantidade, String motivo) {
        if (quantidade <= 0) {
            throw new BusinessException("A quantidade de entrada deve ser maior que zero.");
        }

        Estoque estoque = findById(id);
        estoque.setQuantidadeAtual(estoque.verificarEstoque() + quantidade);

        registrarMovimentacao(estoque, TipoMovimentacao.ENTRADA, quantidade, motivo);

        return estoqueRepository.save(estoque);
    }

    public Estoque removerQuantidade(Long id, int quantidade, String motivo) {
        if (quantidade <= 0) {
            throw new BusinessException("A quantidade de saída deve ser maior que zero.");
        }

        Estoque estoque = findById(id);

        if (estoque.verificarEstoque() < quantidade) {
            throw new BusinessException("Estoque insuficiente para saída.");
        }

        estoque.setQuantidadeAtual(estoque.verificarEstoque() - quantidade);

        registrarMovimentacao(estoque, TipoMovimentacao.SAIDA, quantidade, motivo);

        return estoqueRepository.save(estoque);
    }

    public List<Estoque> buscarProdutosSemEstoque() {
        return estoqueRepository.buscarProdutosSemEstoque();
    }

    public List<Produto> alertaVencimento(int dias) {
        if (dias <= 0) {
            throw new BusinessException("A quantidade de dias deve ser maior que zero.");
        }
        Instant limite = Instant.now().plusSeconds(60L * 60 * 24 * dias);
        return produtoRepository.buscarProdutosProximosDoVencimento(limite);
    }

    private void registrarMovimentacao(Estoque estoque, TipoMovimentacao tipo, int quantidade, String motivo) {
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

        movimentacao.setEstoque(estoque);
        movimentacao.setTipoMovimentacao(tipo);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setMotivo(motivo == null || motivo.isBlank() ? "Movimentação de estoque" : motivo);

        if (tipo == TipoMovimentacao.ENTRADA) {
            movimentacao.registrarEntrada();
        } else if (tipo == TipoMovimentacao.SAIDA) {
            movimentacao.registrarSaida();
        }

        movimentacaoRepository.save(movimentacao);
    }

    private void validarEstoque(EstoqueRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Informe os dados do estoque.");
        }
        if (dto.produtoId() == null || dto.produtoId() <= 0) {
            throw new BusinessException("Informe um produto válido para o estoque.");
        }
        if (dto.quantidadeAtual() == null || dto.quantidadeAtual() < 0) {
            throw new BusinessException("A quantidade atual não pode ser negativa.");
        }
        if (dto.quantidadeMaxima() == null || dto.quantidadeMaxima() <= 0) {
            throw new BusinessException("A quantidade máxima deve ser maior que zero.");
        }
        if (dto.quantidadeAtual() > dto.quantidadeMaxima()) {
            throw new BusinessException("A quantidade atual não pode ultrapassar a quantidade máxima.");
        }
        if (dto.localizacao() == null || dto.localizacao().isBlank()) {
            throw new BusinessException("A localização do estoque é obrigatória.");
        }
    }
}
