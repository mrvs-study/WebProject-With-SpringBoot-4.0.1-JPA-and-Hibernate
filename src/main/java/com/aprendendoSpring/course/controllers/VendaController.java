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

import com.aprendendoSpring.course.dtos.VendaRequestDTO;
import com.aprendendoSpring.course.dtos.VendaResponseDTO;
import com.aprendendoSpring.course.services.VendaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/vendas")
@Tag(
    name = "Venda",
    description = "Endpoints responsáveis pelo registro, consulta, finalização e cancelamento de vendas."
)
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @GetMapping
    @Operation(summary = "Listar vendas")
    public ResponseEntity<List<VendaResponseDTO>> findAll() {
        return ResponseEntity.ok(vendaService.findAll().stream().map(VendaResponseDTO::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Visualizar venda por ID")
    public ResponseEntity<VendaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(VendaResponseDTO.fromEntity(vendaService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Registrar venda")
    public ResponseEntity<VendaResponseDTO> insert(@RequestBody VendaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(VendaResponseDTO.fromEntity(vendaService.insert(dto)));
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar venda")
    public ResponseEntity<VendaResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(VendaResponseDTO.fromEntity(vendaService.finalizarVenda(id)));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar venda")
    public ResponseEntity<VendaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(VendaResponseDTO.fromEntity(vendaService.cancelarVenda(id)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar vendas por status", description = "Consulta nativa relevante para visualizar vendas por status.")
    public ResponseEntity<List<VendaResponseDTO>> buscarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(vendaService.buscarVendasPorStatus(status).stream().map(VendaResponseDTO::fromEntity).toList());
    }
}
