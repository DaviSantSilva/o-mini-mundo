package com.minimundo;

import com.minimundo.model.Role;
import com.minimundo.model.Usuario;
import com.minimundo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class OMiniMundoApplication {
    public static void main(String[] args) {
        SpringApplication.run(OMiniMundoApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (!usuarioRepository.existsByEmail("admin@minimundo.com")) {
                var admin = Usuario.builder()
                        .nome("Administrador")
                        .email("admin@minimundo.com")
                        .senha(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build();
                usuarioRepository.save(admin);
            }

            if (!usuarioRepository.existsByEmail("manager@minimundo.com")) {
                var manager = Usuario.builder()
                        .nome("Gerente")
                        .email("manager@minimundo.com")
                        .senha(passwordEncoder.encode("manager123"))
                        .role(Role.MANAGER)
                        .build();
                usuarioRepository.save(manager);
            }
        };
    }
} 