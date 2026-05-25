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

import com.aprendendoSpring.course.dtos.FuncionarioRequestDTO;
import com.aprendendoSpring.course.dtos.FuncionarioResponseDTO;
import com.aprendendoSpring.course.services.GerenteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/gerentes")
@Tag(
    name = "Gerente",
    description = "Endpoints responsáveis pelas ações do gerente, como cadastro de funcionários e geração de relatórios."
)
public class GerenteController {

    private final GerenteService gerenteService;

    public GerenteController(GerenteService gerenteService) {
        this.gerenteService = gerenteService;
    }
    
    @GetMapping("/{idGerente}/estoquistas")
    @Operation(summary = "Listar estoquistas")
    public ResponseEntity<List<FuncionarioResponseDTO>> listarEstoquistas(@PathVariable Long idGerente) {
        List<FuncionarioResponseDTO> lista = gerenteService.listarEstoquistas(idGerente)
                .stream()
                .map(FuncionarioResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(lista);
    }
    @GetMapping("/{idGerente}/operadores")
    @Operation(summary = "Listar operadores")
    public ResponseEntity<List<FuncionarioResponseDTO>> listarOperadores(@PathVariable Long idGerente) {
        List<FuncionarioResponseDTO> lista = gerenteService.listarOperadores(idGerente)
                .stream()
                .map(FuncionarioResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(lista);
    }


    @GetMapping("/{idGerente}/relatorio")
    @Operation(summary = "Gerar relatório geral do sistema")
    public ResponseEntity<String> gerarRelatorio(@PathVariable Long idGerente) {
        return ResponseEntity.ok(gerenteService.gerarRelatorio(idGerente));
    }

    @PostMapping("/{idGerente}/funcionarios")
    @Operation(summary = "Cadastrar funcionário")
    public ResponseEntity<FuncionarioResponseDTO> cadastrarFuncionario(
            @PathVariable Long idGerente,
            @RequestBody FuncionarioRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(FuncionarioResponseDTO.fromEntity(
                        gerenteService.cadastrarFuncionario(idGerente, dto)
                ));
    }

    @GetMapping("/{idGerente}/funcionarios")
    @Operation(summary = "Listar funcionários")
    public ResponseEntity<List<FuncionarioResponseDTO>> listarFuncionarios(
            @PathVariable Long idGerente) {

        return ResponseEntity.ok(
                gerenteService.listarFuncionarios(idGerente)
                        .stream()
                        .map(FuncionarioResponseDTO::fromEntity)
                        .toList()
        );
    }

    @GetMapping("/{idGerente}/funcionarios/{idFuncionario}")
    @Operation(summary = "Buscar funcionário por ID")
    public ResponseEntity<FuncionarioResponseDTO> buscarFuncionario(
            @PathVariable Long idGerente,
            @PathVariable Long idFuncionario) {

        return ResponseEntity.ok(
                FuncionarioResponseDTO.fromEntity(
                        gerenteService.buscarFuncionario(idGerente, idFuncionario)
                )
        );
    }

    @PutMapping("/{idGerente}/funcionarios/{idFuncionario}")
    @Operation(summary = "Atualizar funcionário")
    public ResponseEntity<FuncionarioResponseDTO> atualizarFuncionario(
            @PathVariable Long idGerente,
            @PathVariable Long idFuncionario,
            @RequestBody FuncionarioRequestDTO dto) {

        return ResponseEntity.ok(
                FuncionarioResponseDTO.fromEntity(
                        gerenteService.atualizarFuncionario(idGerente, idFuncionario, dto)
                )
        );
    }

    @DeleteMapping("/{idGerente}/funcionarios/{idFuncionario}")
    @Operation(summary = "Remover funcionário")
    public ResponseEntity<Void> removerFuncionario(
            @PathVariable Long idGerente,
            @PathVariable Long idFuncionario) {

        gerenteService.removerFuncionario(idGerente, idFuncionario);
        return ResponseEntity.noContent().build();
    }
}