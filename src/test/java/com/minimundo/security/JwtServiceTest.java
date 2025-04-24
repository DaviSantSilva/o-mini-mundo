package com.minimundo.security;

import com.minimundo.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Configurar a chave secreta e tempo de expiração para testes
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000); // 1 segundo de expiração
        
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("senha123");
        userDetails = usuario;
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken(userDetails);
        String email = jwtService.extractUsername(token);

        assertEquals(userDetails.getUsername(), email);
    }

    @Test
    void testIsTokenValid() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenValidInvalid() {
        String token = "token.invalido";
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void testTokenExpired() throws InterruptedException {
        String token = jwtService.generateToken(userDetails);
        // Esperar o token expirar (mais de 1 segundo)
        Thread.sleep(1100);
        
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertFalse(isValid);
    }
} 