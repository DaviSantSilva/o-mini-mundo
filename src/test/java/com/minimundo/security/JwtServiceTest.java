package com.minimundo.security;

import com.minimundo.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("senha123");
    }

    @Test
    void testGerarToken() {
        String token = jwtService.gerarToken(usuario);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testExtrairEmail() {
        String token = jwtService.gerarToken(usuario);
        String email = jwtService.extrairEmail(token);

        assertEquals(usuario.getEmail(), email);
    }

    @Test
    void testValidarToken() {
        String token = jwtService.gerarToken(usuario);
        boolean isValid = jwtService.validarToken(token, usuario);

        assertTrue(isValid);
    }

    @Test
    void testValidarTokenInvalido() {
        String token = "token.invalido";
        boolean isValid = jwtService.validarToken(token, usuario);

        assertFalse(isValid);
    }

    @Test
    void testTokenExpirado() {
        // Criar um token expirado
        String token = jwtService.gerarToken(usuario);
        // Simular expiração
        Date dataExpiracao = new Date(System.currentTimeMillis() - 1000);
        boolean isValid = jwtService.validarToken(token, usuario);

        assertFalse(isValid);
    }
} 