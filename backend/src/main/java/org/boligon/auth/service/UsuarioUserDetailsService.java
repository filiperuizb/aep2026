package org.boligon.auth.service;

import org.boligon.auth.domain.Usuario;
import org.boligon.auth.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UsuarioUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Usuario> encontrado = usuarioRepository.findByEmail(email.trim().toLowerCase());
        if (encontrado.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado.");
        }

        Usuario usuario = encontrado.get();
        String perfil = "ROLE_" + usuario.getPerfil().name();

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getAtivo(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(perfil))
        );
    }
}
