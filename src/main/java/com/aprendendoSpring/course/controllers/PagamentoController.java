package com.aprendendoSpring.course.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aprendendoSpring.course.dtos.PagamentoRequestDTO;
import com.aprendendoSpring.course.dtos.PagamentoResponseDTO;
import com.aprendendoSpring.course.services.PagamentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/pagamentos")
@Tag(
    name = "Pagamento",
    description = "Endpoints responsáveis pelo registro, consulta e processamento de pagamentos."
)
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    @Operation(summary = "Listar pagamentos")
    public ResponseEntity<List<PagamentoResponseDTO>> findAll() {
        return ResponseEntity.ok(pagamentoService.findAll().stream().map(PagamentoResponseDTO::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pagamento por ID")
    public ResponseEntity<PagamentoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(PagamentoResponseDTO.fromEntity(pagamentoService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Registrar pagamento")
    public ResponseEntity<PagamentoResponseDTO> insert(@RequestBody PagamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(PagamentoResponseDTO.fromEntity(pagamentoService.insert(dto)));
    }

    @PutMapping("/{id}/processar")
    @Operation(summary = "Processar pagamento")
    public ResponseEntity<PagamentoResponseDTO> processar(@PathVariable Long id) {
        return ResponseEntity.ok(PagamentoResponseDTO.fromEntity(pagamentoService.processarPagamento(id)));
    }

    @GetMapping("/pendentes")
    @Operation(summary = "Listar pagamentos pendentes", description = "Consulta nativa relevante para verificar pagamentos pendentes.")
    public ResponseEntity<List<PagamentoResponseDTO>> pendentes() {
        return ResponseEntity.ok(pagamentoService.buscarPagamentosPendentes().stream().map(PagamentoResponseDTO::fromEntity).toList());
    }
}
