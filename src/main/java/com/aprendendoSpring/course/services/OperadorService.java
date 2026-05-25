package com.aprendendoSpring.course.services;

import com.aprendendoSpring.course.dtos.PagamentoRequestDTO;
import com.aprendendoSpring.course.dtos.VendaRequestDTO;
import com.aprendendoSpring.course.entities.Funcionario;
import com.aprendendoSpring.course.entities.ItemVenda;
import com.aprendendoSpring.course.entities.Pagamento;
import com.aprendendoSpring.course.entities.Venda;
import com.aprendendoSpring.course.entities.enums.CargoFuncionario;
import com.aprendendoSpring.course.exceptions.BusinessException;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OperadorService {

    private final FuncionarioService funcionarioService;
    private final VendaService vendaService;
    private final PagamentoService pagamentoService;

    public OperadorService(
            FuncionarioService funcionarioService,
            VendaService vendaService,
            PagamentoService pagamentoService) {

        this.funcionarioService = funcionarioService;
        this.vendaService = vendaService;
        this.pagamentoService = pagamentoService;
    }

    public Venda registrarVenda(Long idOperador, VendaRequestDTO dto) {
        validarOperador(idOperador);
        return vendaService.insert(dto);
    }

    public Pagamento registrarPagamento(Long idOperador, PagamentoRequestDTO dto) {
        validarOperador(idOperador);
        return pagamentoService.insert(dto);
    }

    public Pagamento processarPagamento(Long idOperador, Long idPagamento) {
        validarOperador(idOperador);
        return pagamentoService.processarPagamento(idPagamento);
    }

    public Venda cancelarVenda(Long idOperador, Long idVenda) {
        validarOperador(idOperador);
        return vendaService.cancelarVenda(idVenda);
    }

    public String emitirNota(Long idOperador, Long idVenda) {
        validarOperador(idOperador);

        Venda venda = vendaService.findById(idVenda);

        String itens = venda.getItens()
                .stream()
                .map(this::formatarItemNota)
                .collect(Collectors.joining("\n"));

        return """
                NOTA EASY MARKET

                Venda: %d
                Cliente: %s
                Status: %s
                Total: R$ %.2f

                Itens:
                %s
                """.formatted(
                venda.getIdVenda(),
                venda.getCliente() == null ? "Cliente não informado" : venda.getCliente().getNome(),
                venda.getStatusVenda(),
                venda.getTotal(),
                itens.isBlank() ? "Nenhum item encontrado." : itens
        );
    }

    private String formatarItemNota(ItemVenda item) {
        String nomeProduto = item.getProduto() == null ? "Produto não informado" : item.getProduto().getNome();

        return "- " + nomeProduto
                + " | Quantidade: " + item.getQuantidade()
                + " | Preço unitário: R$ " + item.getPrecoUnitario()
                + " | Subtotal: R$ " + item.getSubtotal();
    }

    private void validarOperador(Long idOperador) {
        Funcionario funcionario = funcionarioService.findById(idOperador);

        if (funcionario.getCargo() != CargoFuncionario.OPERADOR) {
            throw new BusinessException("Apenas funcionário com cargo OPERADOR pode executar esta operação.");
        }
    }
}