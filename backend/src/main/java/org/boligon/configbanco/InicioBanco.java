package org.boligon.configbanco;

import org.boligon.auth.domain.Usuario;
import org.boligon.auth.repository.UsuarioRepository;
import org.boligon.enums.PerfilUsuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
public class InicioBanco {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorSenha;

    public InicioBanco(UsuarioRepository usuarioRepository, PasswordEncoder codificadorSenha) {
        this.usuarioRepository = usuarioRepository;
        this.codificadorSenha = codificadorSenha;
    }

    @PostConstruct
    public void inicializarBanco() {
        garantirGestorPadrao();
        garantirCidadaoPadrao();
        codificarSenhasEmTextoPuro();
    }

    private void garantirGestorPadrao() {
        if (!usuarioRepository.existsByEmail("admin@admin.com")) {
            salvarUsuario("Administrador Teste", "admin@admin.com", "123", PerfilUsuario.GESTOR);
        }
    }

    private void garantirCidadaoPadrao() {
        if (!usuarioRepository.existsByEmail("cidadao@test.com")) {
            salvarUsuario("Cidadão Teste", "cidadao@test.com", "123", PerfilUsuario.CIDADAO);
        }
    }

    private void salvarUsuario(String nome, String email, String senha, PerfilUsuario perfil) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(codificadorSenha.encode(senha));
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    private void codificarSenhasEmTextoPuro() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            if (!usuario.getSenha().startsWith("$2a$")) {
                usuario.setSenha(codificadorSenha.encode(usuario.getSenha()));
                usuarioRepository.save(usuario);
            }
        }
    }
}
