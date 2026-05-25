package com.aprendendoSpring.course.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aprendendoSpring.course.dtos.FuncionarioRequestDTO;
import com.aprendendoSpring.course.entities.Funcionario;
import com.aprendendoSpring.course.entities.enums.CargoFuncionario;
import com.aprendendoSpring.course.exceptions.BusinessException;
import com.aprendendoSpring.course.repositories.ClienteRepository;
import com.aprendendoSpring.course.repositories.FuncionarioRepository;
import com.aprendendoSpring.course.repositories.ProdutoRepository;
import com.aprendendoSpring.course.repositories.VendaRepository;

@Service
public class GerenteService {

    private final FuncionarioService funcionarioService;
    private final FuncionarioRepository funcionarioRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final VendaRepository vendaRepository;

    public GerenteService(
            FuncionarioService funcionarioService,
            FuncionarioRepository funcionarioRepository,
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository,
            VendaRepository vendaRepository) {

        this.funcionarioService = funcionarioService;
        this.funcionarioRepository = funcionarioRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.vendaRepository = vendaRepository;
    }

    public String gerarRelatorio(Long idGerente) {
        validarGerente(idGerente);

        long totalFuncionarios = funcionarioRepository.count();
        long totalProdutos = produtoRepository.count();
        long totalClientes = clienteRepository.count();
        long totalVendas = vendaRepository.count();

        return """
                RELATÓRIO GERAL - EASY MARKET

                Total de funcionários: %d
                Total de produtos: %d
                Total de clientes: %d
                Total de vendas: %d
                """.formatted(
                totalFuncionarios,
                totalProdutos,
                totalClientes,
                totalVendas
        );
    }

    public Funcionario cadastrarFuncionario(Long idGerente, FuncionarioRequestDTO dto) {
        validarGerente(idGerente);
        return funcionarioService.insert(dto);
    }

    public List<Funcionario> listarFuncionarios(Long idGerente) {
        validarGerente(idGerente);
        return funcionarioService.findAll();
    }

    public Funcionario buscarFuncionario(Long idGerente, Long idFuncionario) {
        validarGerente(idGerente);
        return funcionarioService.findById(idFuncionario);
    }

    public Funcionario atualizarFuncionario(Long idGerente, Long idFuncionario, FuncionarioRequestDTO dto) {
        validarGerente(idGerente);
        return funcionarioService.update(idFuncionario, dto);
    }

    public void removerFuncionario(Long idGerente, Long idFuncionario) {
        validarGerente(idGerente);
        funcionarioService.delete(idFuncionario);
    }

    private void validarGerente(Long idGerente) {
        Funcionario funcionario = funcionarioService.findById(idGerente);

        if (funcionario.getCargo() != CargoFuncionario.GERENTE) {
            throw new BusinessException("Apenas funcionário com cargo GERENTE pode executar esta operação.");
        }
    }
}