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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aprendendoSpring.course.dtos.EstoqueRequestDTO;
import com.aprendendoSpring.course.dtos.EstoqueResponseDTO;
import com.aprendendoSpring.course.services.EstoqueService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/estoques")
@Tag(
    name = "Estoque",
    description = "Endpoints responsáveis pelo controle de estoque, localização, quantidade e alertas."
)
public class EstoqueController {

	private final EstoqueService estoqueService;

	public EstoqueController(EstoqueService estoqueService) {
		this.estoqueService = estoqueService;
	}

	@GetMapping
	@Operation(summary = "Listar estoques")
	public ResponseEntity<List<EstoqueResponseDTO>> findAll() {
		return ResponseEntity.ok(estoqueService.findAll().stream().map(EstoqueResponseDTO::fromEntity).toList());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar estoque por ID")
	public ResponseEntity<EstoqueResponseDTO> findById(@PathVariable Long id) {
		return ResponseEntity.ok(EstoqueResponseDTO.fromEntity(estoqueService.findById(id)));
	}

	@PostMapping
	@Operation(summary = "Cadastrar estoque")
	public ResponseEntity<EstoqueResponseDTO> insert(@RequestBody EstoqueRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(EstoqueResponseDTO.fromEntity(estoqueService.insert(dto)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar estoque")
	public ResponseEntity<EstoqueResponseDTO> update(@PathVariable Long id, @RequestBody EstoqueRequestDTO dto) {
		return ResponseEntity.ok(EstoqueResponseDTO.fromEntity(estoqueService.update(id, dto)));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Remover estoque")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		estoqueService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/entrada")
	@Operation(summary = "Adicionar quantidade ao estoque")
	public ResponseEntity<EstoqueResponseDTO> adicionarQuantidade(@PathVariable Long id, @RequestParam int quantidade,
			@RequestParam(defaultValue = "Entrada manual") String motivo) {
		return ResponseEntity
				.ok(EstoqueResponseDTO.fromEntity(estoqueService.adicionarQuantidade(id, quantidade, motivo)));
	}

	@PutMapping("/{id}/saida")
	@Operation(summary = "Remover quantidade do estoque")
	public ResponseEntity<EstoqueResponseDTO> removerQuantidade(@PathVariable Long id, @RequestParam int quantidade,
			@RequestParam(defaultValue = "Saída manual") String motivo) {
		return ResponseEntity
				.ok(EstoqueResponseDTO.fromEntity(estoqueService.removerQuantidade(id, quantidade, motivo)));
	}

	@GetMapping("/sem-estoque")
	@Operation(summary = "Produtos sem estoque", description = "Consulta nativa relevante para localizar produtos sem saldo em estoque.")
	public ResponseEntity<List<EstoqueResponseDTO>> produtosSemEstoque() {
		return ResponseEntity
				.ok(estoqueService.buscarProdutosSemEstoque().stream().map(EstoqueResponseDTO::fromEntity).toList());
	}
}
