package org.boligon.auth.service;

import org.boligon.auth.domain.Usuario;
import org.boligon.auth.repository.UsuarioRepository;
import org.boligon.enums.PerfilUsuario;
import org.boligon.exception.ValidacaoException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void registrar(String nome, String email, String senha) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("O nome é obrigatório.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new ValidacaoException("O e-mail é obrigatório.");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new ValidacaoException("A senha é obrigatória.");
        }
        if (usuarioRepository.existsByEmail(email.trim().toLowerCase())) {
            throw new ValidacaoException("Já existe um usuário com o email vinculado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome.trim());
        novoUsuario.setEmail(email.trim().toLowerCase());
        novoUsuario.setSenha(senha);
        novoUsuario.setPerfil(PerfilUsuario.CIDADAO);
        novoUsuario.setAtivo(true);

        usuarioRepository.save(novoUsuario);
    }

    public Usuario login(String email, String senha) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidacaoException("O e-mail é obrigatório.");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new ValidacaoException("A senha é obrigatória.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase()).orElse(null);

        if (usuario == null) {
            throw new ValidacaoException("Usuário não encontrado.");
        }

        if (!usuario.getSenha().equals(senha)) {
            throw new ValidacaoException("Senha inválida");
        }

        if (!usuario.getAtivo()) {
            throw new ValidacaoException("Usuário Inativo.");
        }

        return usuario;
    }

    public Usuario buscarPorId(Long id) {
        if (id == null) {
            throw new ValidacaoException("O id do usuário é obrigatório.");
        }
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Usuário não encontrado."));
    }
}
