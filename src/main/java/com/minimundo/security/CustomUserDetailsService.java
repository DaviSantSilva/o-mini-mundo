package com.minimundo.security;

import com.minimundo.model.Usuario;
import com.minimundo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @PostConstruct
    public void init() {
        log.info("CustomUserDetailsService inicializado com sucesso");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Tentando carregar usuário com email: {}", email);
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado com o email: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
                });
    }
} 