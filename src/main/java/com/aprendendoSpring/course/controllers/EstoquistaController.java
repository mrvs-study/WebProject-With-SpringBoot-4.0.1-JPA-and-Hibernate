package com.aprendendoSpring.course.controllers;

import com.aprendendoSpring.course.dtos.MovimentacaoEstoqueRequestDTO;
import com.aprendendoSpring.course.dtos.MovimentacaoEstoqueResponseDTO;
import com.aprendendoSpring.course.dtos.ProdutoRequestDTO;
import com.aprendendoSpring.course.dtos.ProdutoResponseDTO;
import com.aprendendoSpring.course.services.EstoquistaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoquistas")
@Tag(name = "Estoquista", description = "Endpoints responsáveis pelas ações do estoquista, como registrar produtos, atualizar produtos, remover produtos e movimentar estoque.")
public class EstoquistaController {

	private final EstoquistaService estoquistaService;

	public EstoquistaController(EstoquistaService estoquistaService) {
		this.estoquistaService = estoquistaService;
	}

	@GetMapping("/{idEstoquista}/produtos")
	@Operation(summary = "Listar produtos", description = "Lista todos os produtos cadastrados. Apenas funcionários com cargo ESTOQUISTA podem executar esta ação.")
	public ResponseEntity<List<ProdutoResponseDTO>> listarProdutos(@PathVariable Long idEstoquista) {
		List<ProdutoResponseDTO> produtos = estoquistaService.listarProdutos(idEstoquista).stream()
				.map(ProdutoResponseDTO::fromEntity).toList();

		return ResponseEntity.ok(produtos);
	}

	@PostMapping("/{idEstoquista}/produtos")
	@Operation(summary = "Registrar produto", description = "Cadastra um novo produto no sistema. Esta ação representa a função registrarProduto() do Estoquista no diagrama de classes.")
	public ResponseEntity<ProdutoResponseDTO> registrarProduto(@PathVariable Long idEstoquista,
			@RequestBody ProdutoRequestDTO dto) {

		ProdutoResponseDTO response = ProdutoResponseDTO
				.fromEntity(estoquistaService.registrarProduto(idEstoquista, dto));

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{idEstoquista}/produtos/{idProduto}")
	@Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente, como nome, categoria, preço, validade ou imagem.")
	public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long idEstoquista,
			@PathVariable Long idProduto, @RequestBody ProdutoRequestDTO dto) {

		ProdutoResponseDTO response = ProdutoResponseDTO
				.fromEntity(estoquistaService.atualizarProduto(idEstoquista, idProduto, dto));

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{idEstoquista}/produtos/{idProduto}")
	@Operation(summary = "Remover produto", description = "Remove um produto do sistema pelo seu ID.")
	public ResponseEntity<Void> removerProduto(@PathVariable Long idEstoquista, @PathVariable Long idProduto) {

		estoquistaService.removerProduto(idEstoquista, idProduto);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{idEstoquista}/produtos/{idProduto}/localizacao")
	@Operation(summary = "Localizar produto", description = "Localiza um produto no estoque e retorna sua localização, quantidade atual e quantidade máxima.")
	public ResponseEntity<String> localizarProduto(@PathVariable Long idEstoquista, @PathVariable Long idProduto) {

		return ResponseEntity.ok(estoquistaService.localizarProduto(idEstoquista, idProduto));
	}

	@PostMapping("/{idEstoquista}/movimentacoes/entrada")
	@Operation(summary = "Registrar entrada no estoque", description = "Registra uma movimentação de entrada no estoque, aumentando a quantidade disponível do produto.")
	public ResponseEntity<MovimentacaoEstoqueResponseDTO> registrarEntrada(@PathVariable Long idEstoquista,
			@RequestBody MovimentacaoEstoqueRequestDTO dto) {

		MovimentacaoEstoqueResponseDTO response = MovimentacaoEstoqueResponseDTO
				.fromEntity(estoquistaService.registrarEntrada(idEstoquista, dto));

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/{idEstoquista}/movimentacoes/saida")
	@Operation(summary = "Registrar saída no estoque", description = "Registra uma movimentação de saída no estoque, diminuindo a quantidade disponível do produto.")
	public ResponseEntity<MovimentacaoEstoqueResponseDTO> registrarSaida(@PathVariable Long idEstoquista,
			@RequestBody MovimentacaoEstoqueRequestDTO dto) {

		MovimentacaoEstoqueResponseDTO response = MovimentacaoEstoqueResponseDTO
				.fromEntity(estoquistaService.registrarSaida(idEstoquista, dto));

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}