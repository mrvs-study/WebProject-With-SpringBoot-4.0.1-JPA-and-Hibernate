package com.aprendendoSpring.course.controllers;

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
import com.aprendendoSpring.course.dtos.VendaRequestDTO;
import com.aprendendoSpring.course.dtos.VendaResponseDTO;
import com.aprendendoSpring.course.services.OperadorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/operadores")
@Tag(name = "Operador", description = "Endpoints responsáveis pelas ações do operador, como registrar vendas, pagamentos, cancelar vendas e emitir nota.")
public class OperadorController {

	private final OperadorService operadorService;

	public OperadorController(OperadorService operadorService) {
		this.operadorService = operadorService;
	}

	@PostMapping("/{idOperador}/vendas")
	@Operation(summary = "Registrar venda")
	public ResponseEntity<VendaResponseDTO> registrarVenda(@PathVariable Long idOperador,
			@RequestBody VendaRequestDTO dto) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(VendaResponseDTO.fromEntity(operadorService.registrarVenda(idOperador, dto)));
	}

	@PostMapping("/{idOperador}/pagamentos")
	@Operation(summary = "Registrar pagamento")
	public ResponseEntity<PagamentoResponseDTO> registrarPagamento(@PathVariable Long idOperador,
			@RequestBody PagamentoRequestDTO dto) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(PagamentoResponseDTO.fromEntity(operadorService.registrarPagamento(idOperador, dto)));
	}

	@PutMapping("/{idOperador}/pagamentos/{idPagamento}/processar")
	@Operation(summary = "Processar pagamento")
	public ResponseEntity<PagamentoResponseDTO> processarPagamento(@PathVariable Long idOperador,
			@PathVariable Long idPagamento) {

		return ResponseEntity
				.ok(PagamentoResponseDTO.fromEntity(operadorService.processarPagamento(idOperador, idPagamento)));
	}

	@PutMapping("/{idOperador}/vendas/{idVenda}/cancelar")
	@Operation(summary = "Cancelar venda")
	public ResponseEntity<VendaResponseDTO> cancelarVenda(@PathVariable Long idOperador, @PathVariable Long idVenda) {

		return ResponseEntity.ok(VendaResponseDTO.fromEntity(operadorService.cancelarVenda(idOperador, idVenda)));
	}

	@GetMapping("/{idOperador}/vendas/{idVenda}/nota")
	@Operation(summary = "Emitir nota da venda")
	public ResponseEntity<String> emitirNota(@PathVariable Long idOperador, @PathVariable Long idVenda) {

		return ResponseEntity.ok(operadorService.emitirNota(idOperador, idVenda));
	}
}