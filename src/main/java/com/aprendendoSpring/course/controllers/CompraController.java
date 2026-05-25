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

import com.aprendendoSpring.course.dtos.CompraRequestDTO;
import com.aprendendoSpring.course.dtos.CompraResponseDTO;
import com.aprendendoSpring.course.services.CompraService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/compras")
@Tag(
    name = "Compra",
    description = "Endpoints responsáveis pelo registro e consulta de compras realizadas com fornecedores."
)
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    @Operation(summary = "Listar compras")
    public ResponseEntity<List<CompraResponseDTO>> findAll() {
        return ResponseEntity.ok(compraService.findAll().stream().map(CompraResponseDTO::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar compra por ID")
    public ResponseEntity<CompraResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(CompraResponseDTO.fromEntity(compraService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Registrar compra")
    public ResponseEntity<CompraResponseDTO> insert(@RequestBody CompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CompraResponseDTO.fromEntity(compraService.insert(dto)));
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar compra")
    public ResponseEntity<CompraResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(CompraResponseDTO.fromEntity(compraService.finalizarCompra(id)));
    }

    @GetMapping("/fornecedor/{fornecedorId}")
    @Operation(summary = "Listar compras por fornecedor", description = "Consulta nativa relevante para acompanhar compras feitas por fornecedor.")
    public ResponseEntity<List<CompraResponseDTO>> comprasPorFornecedor(@PathVariable Long fornecedorId) {
        return ResponseEntity.ok(compraService.buscarComprasPorFornecedor(fornecedorId).stream().map(CompraResponseDTO::fromEntity).toList());
    }
}
