package com.aprendendoSpring.course.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aprendendoSpring.course.dtos.FornecedorRequestDTO;
import com.aprendendoSpring.course.dtos.FornecedorResponseDTO;
import com.aprendendoSpring.course.services.FornecedorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/fornecedores")
@Tag(
    name = "Fornecedor",
    description = "Endpoints responsáveis pelo cadastro, consulta, atualização e remoção de fornecedores."
)
public class FornecedorController {

	private final FornecedorService fornecedorService;

	public FornecedorController(FornecedorService fornecedorService) {
		this.fornecedorService = fornecedorService;
	}

	@GetMapping
	@Operation(summary = "Listar fornecedores")
	public ResponseEntity<List<FornecedorResponseDTO>> findAll() {
		return ResponseEntity.ok(fornecedorService.findAll().stream().map(FornecedorResponseDTO::fromEntity).toList());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar fornecedor por ID")
	public ResponseEntity<FornecedorResponseDTO> findById(@PathVariable Long id) {
		return ResponseEntity.ok(FornecedorResponseDTO.fromEntity(fornecedorService.findById(id)));
	}

	@PostMapping
	@Operation(summary = "Cadastrar fornecedor")
	public ResponseEntity<FornecedorResponseDTO> insert(@RequestBody FornecedorRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(FornecedorResponseDTO.fromEntity(fornecedorService.insert(dto)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar fornecedor")
	public ResponseEntity<FornecedorResponseDTO> update(@PathVariable Long id, @RequestBody FornecedorRequestDTO dto) {
		return ResponseEntity.ok(FornecedorResponseDTO.fromEntity(fornecedorService.update(id, dto)));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Remover fornecedor")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		fornecedorService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
