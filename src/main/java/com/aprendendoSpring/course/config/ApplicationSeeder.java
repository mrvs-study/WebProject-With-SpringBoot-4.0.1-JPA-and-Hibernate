package com.aprendendoSpring.course.config;

import com.aprendendoSpring.course.entities.Funcionario;
import com.aprendendoSpring.course.entities.enums.CargoFuncionario;
import com.aprendendoSpring.course.repositories.FuncionarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ApplicationSeeder implements ApplicationRunner {

    private final FuncionarioRepository funcionarioRepository;

    public ApplicationSeeder(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!funcionarioRepository.existsByEmail("admin@easymarket.com")) {
            Funcionario admin = new Funcionario(
                    null,
                    "Administrador",
                    "000.000.000-00",
                    "admin@easymarket.com",
                    null,
                    "admin1234",
                    CargoFuncionario.GERENTE,
                    5000.0,
                    Instant.now()
            );
            funcionarioRepository.save(admin);
        }
    }
}
