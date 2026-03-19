package org.boligon.service;

import org.boligon.entity.Usuario;
import org.boligon.enums.PerfilUsuario;
import org.boligon.exception.ValidacaoException;
import org.boligon.repository.UsuarioRepository;

public class AuthService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    public void registrar(String nome, String email, String senha) {
        if(usuarioRepository.existePorEmail(email)) {
            throw new ValidacaoException("Já existe um usuário com o email vinculado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email.trim().toLowerCase());
        novoUsuario.setSenha(senha);
        novoUsuario.setPerfil(PerfilUsuario.CIDADAO);
        novoUsuario.setAtivo(true);

        usuarioRepository.salvar(novoUsuario);
    }

    public Usuario login(String email, String senha) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email.trim().toLowerCase());

        if(usuario == null) {
            throw new ValidacaoException("Usuário não encontrado.");
        }

        if(!usuario.getSenha().equals(senha)) {
            throw new ValidacaoException("Senha inválida");
        }

        if(!usuario.getAtivo()) {
            throw new ValidacaoException("Usuário Inativo.");
        }

        return usuario;
    }
}
