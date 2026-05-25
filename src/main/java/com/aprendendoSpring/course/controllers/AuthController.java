package com.aprendendoSpring.course.controllers;

import com.aprendendoSpring.course.dtos.FuncionarioResponseDTO;
import com.aprendendoSpring.course.dtos.LoginRequestDTO;
import com.aprendendoSpring.course.services.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final FuncionarioService funcionarioService;

    public AuthController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar funcionário")
    public ResponseEntity<FuncionarioResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return funcionarioService.autenticar(dto.email(), dto.senha())
                .map(f -> ResponseEntity.ok(FuncionarioResponseDTO.fromEntity(f)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
