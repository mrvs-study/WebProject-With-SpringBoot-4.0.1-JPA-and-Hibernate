package com.aprendendoSpring.course.services;

import com.aprendendoSpring.course.dtos.PagamentoRequestDTO;
import com.aprendendoSpring.course.entities.Pagamento;
import com.aprendendoSpring.course.entities.Venda;
import com.aprendendoSpring.course.entities.enums.StatusVenda;
import com.aprendendoSpring.course.exceptions.BusinessException;
import com.aprendendoSpring.course.exceptions.ResourceNotFoundException;
import com.aprendendoSpring.course.repositories.PagamentoRepository;
import com.aprendendoSpring.course.repositories.VendaRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final VendaRepository vendaRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository, VendaRepository vendaRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.vendaRepository = vendaRepository;
    }

    public List<Pagamento> findAll() {
        return pagamentoRepository.findAll();
    }

    public Pagamento findById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Informe um ID de pagamento válido.");
        }

        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado. ID: " + id));
    }

    public Pagamento insert(PagamentoRequestDTO dto) {
        validarPagamento(dto);

        Venda venda = vendaRepository.findById(dto.vendaId())
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada. ID: " + dto.vendaId()));

        if (venda.getStatusVenda() == StatusVenda.CANCELADA) {
            throw new BusinessException("Não é possível pagar uma venda cancelada.");
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setVenda(venda);
        pagamento.setFormaPagamento(dto.formaPagamento());
        pagamento.setValor(dto.valor());

        venda.setPagamento(pagamento);

        return pagamentoRepository.save(pagamento);
    }

    public Pagamento processarPagamento(Long id) {
        Pagamento pagamento = findById(id);

        boolean aprovado = pagamento.processarPagamento();

        if (aprovado && pagamento.getVenda() != null) {
            Venda venda = pagamento.getVenda();
            venda.finalizarVenda();
            vendaRepository.save(venda);
        }

        return pagamentoRepository.save(pagamento);
    }

    public List<Pagamento> buscarPagamentosPendentes() {
        return pagamentoRepository.buscarPagamentosPendentes();
    }

    private void validarPagamento(PagamentoRequestDTO dto) {
        if (dto == null) {
            throw new BusinessException("Informe os dados do pagamento.");
        }

        if (dto.vendaId() == null || dto.vendaId() <= 0) {
            throw new BusinessException("Informe uma venda válida para o pagamento.");
        }

        if (dto.formaPagamento() == null) {
            throw new BusinessException("A forma de pagamento é obrigatória.");
        }

        if (dto.valor() == null || dto.valor() <= 0) {
            throw new BusinessException("O valor do pagamento deve ser maior que zero.");
        }
    }
}