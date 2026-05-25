package com.aprendendoSpring.course.dtos;

import com.aprendendoSpring.course.entities.Estoque;
import com.aprendendoSpring.course.entities.MovimentacaoEstoque;
import com.aprendendoSpring.course.entities.Produto;
import com.aprendendoSpring.course.entities.enums.TipoMovimentacao;

import java.time.Instant;

public record MovimentacaoEstoqueResponseDTO(
        Long idMovimentacao,
        Long estoqueId,
        Long produtoId,
        String produtoNome,
        TipoMovimentacao tipoMovimentacao,
        Integer quantidade,
        String motivo,
        Instant dataMovimentacao
) {
    public static MovimentacaoEstoqueResponseDTO fromEntity(MovimentacaoEstoque movimentacao) {
        Estoque estoque = movimentacao.getEstoque();
        Produto produto = estoque == null ? null : estoque.getProduto();

        return new MovimentacaoEstoqueResponseDTO(
                movimentacao.getIdMovimentacao(),
                estoque == null ? null : estoque.getIdEstoque(),
                produto == null ? null : produto.getIdProduto(),
                produto == null ? null : produto.getNome(),
                movimentacao.getTipoMovimentacao(),
                movimentacao.getQuantidade(),
                movimentacao.getMotivo(),
                movimentacao.getDataMovimentacao()
        );
    }
}