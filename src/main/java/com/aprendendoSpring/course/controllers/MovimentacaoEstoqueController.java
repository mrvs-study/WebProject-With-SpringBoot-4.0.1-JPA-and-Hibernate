package com.aprendendoSpring.course.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aprendendoSpring.course.dtos.MovimentacaoEstoqueRequestDTO;
import com.aprendendoSpring.course.dtos.MovimentacaoEstoqueResponseDTO;
import com.aprendendoSpring.course.services.MovimentacaoEstoqueService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/movimentacoes-estoque")
@Tag(
    name = "Movimentação de Estoque",
    description = "Endpoints responsáveis pela consulta e controle das entradas e saídas de estoque."
)
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoService;

    public MovimentacaoEstoqueController(MovimentacaoEstoqueService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping
    @Operation(summary = "Listar movimentações de estoque")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> findAll() {
        return ResponseEntity.ok(movimentacaoService.findAll().stream().map(MovimentacaoEstoqueResponseDTO::fromEntity).toList());
    }

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada de estoque")
    public ResponseEntity<MovimentacaoEstoqueResponseDTO> registrarEntrada(@RequestBody MovimentacaoEstoqueRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoEstoqueResponseDTO.fromEntity(movimentacaoService.registrarEntrada(dto)));
    }

    @PostMapping("/saida")
    @Operation(summary = "Registrar saída de estoque")
    public ResponseEntity<MovimentacaoEstoqueResponseDTO> registrarSaida(@RequestBody MovimentacaoEstoqueRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoEstoqueResponseDTO.fromEntity(movimentacaoService.registrarSaida(dto)));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar movimentações por tipo", description = "Consulta nativa relevante para separar entradas e saídas de estoque.")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(movimentacaoService.buscarPorTipoMovimentacao(tipo).stream().map(MovimentacaoEstoqueResponseDTO::fromEntity).toList());
    }
}
