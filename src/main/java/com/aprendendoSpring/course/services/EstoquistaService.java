package com.aprendendoSpring.course.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aprendendoSpring.course.dtos.MovimentacaoEstoqueRequestDTO;
import com.aprendendoSpring.course.dtos.ProdutoRequestDTO;
import com.aprendendoSpring.course.entities.Estoque;
import com.aprendendoSpring.course.entities.Funcionario;
import com.aprendendoSpring.course.entities.MovimentacaoEstoque;
import com.aprendendoSpring.course.entities.Produto;
import com.aprendendoSpring.course.entities.enums.CargoFuncionario;
import com.aprendendoSpring.course.exceptions.BusinessException;
import com.aprendendoSpring.course.exceptions.ResourceNotFoundException;
import com.aprendendoSpring.course.repositories.EstoqueRepository;

@Service
public class EstoquistaService {

    private final FuncionarioService funcionarioService;
    private final ProdutoService produtoService;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private final EstoqueRepository estoqueRepository;

    public EstoquistaService(
            FuncionarioService funcionarioService,
            ProdutoService produtoService,
            MovimentacaoEstoqueService movimentacaoEstoqueService,
            EstoqueRepository estoqueRepository) {

        this.funcionarioService = funcionarioService;
        this.produtoService = produtoService;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
        this.estoqueRepository = estoqueRepository;
    }

    public List<Produto> listarProdutos(Long idEstoquista) {
        validarEstoquista(idEstoquista);
        return produtoService.findAll();
    }

    public Produto registrarProduto(Long idEstoquista, ProdutoRequestDTO dto) {
        validarEstoquista(idEstoquista);
        return produtoService.insert(dto);
    }

    public Produto atualizarProduto(Long idEstoquista, Long idProduto, ProdutoRequestDTO dto) {
        validarEstoquista(idEstoquista);
        return produtoService.update(idProduto, dto);
    }

    public void removerProduto(Long idEstoquista, Long idProduto) {
        validarEstoquista(idEstoquista);
        produtoService.delete(idProduto);
    }

    public String localizarProduto(Long idEstoquista, Long idProduto) {
        validarEstoquista(idEstoquista);

        Estoque estoque = estoqueRepository.findByProduto_IdProduto(idProduto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estoque não encontrado para o produto ID: " + idProduto
                ));

        Produto produto = estoque.getProduto();

        return """
                Produto localizado.

                Produto: %s
                Localização: %s
                Quantidade atual: %d
                Quantidade máxima: %d
                """.formatted(
                produto == null ? "Produto não informado" : produto.getNome(),
                estoque.getLocalizacao(),
                estoque.getQuantidadeAtual(),
                estoque.getQuantidadeMaxima()
        );
    }

    public MovimentacaoEstoque registrarEntrada(Long idEstoquista, MovimentacaoEstoqueRequestDTO dto) {
        validarEstoquista(idEstoquista);
        return movimentacaoEstoqueService.registrarEntrada(dto);
    }

    public MovimentacaoEstoque registrarSaida(Long idEstoquista, MovimentacaoEstoqueRequestDTO dto) {
        validarEstoquista(idEstoquista);
        return movimentacaoEstoqueService.registrarSaida(dto);
    }

    private void validarEstoquista(Long idEstoquista) {
        Funcionario funcionario = funcionarioService.findById(idEstoquista);

        if (funcionario.getCargo() != CargoFuncionario.ESTOQUISTA) {
            throw new BusinessException("Apenas funcionário com cargo ESTOQUISTA pode executar esta operação.");
        }
    }
}