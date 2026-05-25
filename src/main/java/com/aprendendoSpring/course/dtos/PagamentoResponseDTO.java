package com.aprendendoSpring.course.dtos;

import com.aprendendoSpring.course.entities.Pagamento;
import com.aprendendoSpring.course.entities.enums.FormaPagamento;
import com.aprendendoSpring.course.entities.enums.StatusPagamento;

import java.time.Instant;

public record PagamentoResponseDTO(
        Long idPagamento,
        Long vendaId,
        Instant momento,
        Double valor,
        FormaPagamento formaPagamento,
        StatusPagamento status
) {
    public static PagamentoResponseDTO fromEntity(Pagamento pagamento) {
        return new PagamentoResponseDTO(
                pagamento.getIdPagamento(),
                pagamento.getVenda() == null ? null : pagamento.getVenda().getIdVenda(),
                pagamento.getMomento(),
                pagamento.getValor(),
                pagamento.getFormaPagamento(),
                pagamento.getStatus()
        );
    }
}