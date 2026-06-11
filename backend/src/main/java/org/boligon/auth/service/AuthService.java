package org.boligon.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.boligon.auth.domain.Usuario;
import org.boligon.auth.repository.UsuarioRepository;
import org.boligon.enums.PerfilUsuario;
import org.boligon.exception.ValidacaoException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorSenha;
    private final AuthenticationManager gerenciadorAutenticacao;
    private final SecurityContextRepository repositorioContexto = new HttpSessionSecurityContextRepository();

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder codificadorSenha,
                       AuthenticationManager gerenciadorAutenticacao) {
        this.usuarioRepository = usuarioRepository;
        this.codificadorSenha = codificadorSenha;
        this.gerenciadorAutenticacao = gerenciadorAutenticacao;
    }

    public Usuario fazerLogin(String email, String senha,
                              HttpServletRequest request, HttpServletResponse response) {
        validarTextoObrigatorio(email, "O e-mail é obrigatório.");
        validarTextoObrigatorio(senha, "A senha é obrigatória.");

        String emailNormalizado = email.trim().toLowerCase();
        Authentication autenticacao = validarCredenciais(emailNormalizado, senha);
        salvarSessao(autenticacao, request, response);

        return buscarPorEmail(emailNormalizado);
    }

    public void encerrarSessao(HttpServletRequest request, HttpServletResponse response) {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, autenticacao);
    }

    public void registrar(String nome, String email, String senha) {
        validarTextoObrigatorio(nome, "O nome é obrigatório.");
        validarTextoObrigatorio(email, "O e-mail é obrigatório.");
        validarTextoObrigatorio(senha, "A senha é obrigatória.");

        String emailNormalizado = email.trim().toLowerCase();
        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new ValidacaoException("Já existe um usuário com o email vinculado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome.trim());
        novoUsuario.setEmail(emailNormalizado);
        novoUsuario.setSenha(codificadorSenha.encode(senha));
        novoUsuario.setPerfil(PerfilUsuario.CIDADAO);
        novoUsuario.setAtivo(true);

        usuarioRepository.save(novoUsuario);
    }

    public Usuario buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidacaoException("O e-mail é obrigatório.");
        }

        Optional<Usuario> encontrado = usuarioRepository.findByEmail(email.trim().toLowerCase());
        if (encontrado.isEmpty()) {
            throw new ValidacaoException("Usuário não encontrado.");
        }
        return encontrado.get();
    }

    public Usuario buscarPorId(Long id) {
        if (id == null) {
            throw new ValidacaoException("O id do usuário é obrigatório.");
        }

        Optional<Usuario> encontrado = usuarioRepository.findById(id);
        if (encontrado.isEmpty()) {
            throw new ValidacaoException("Usuário não encontrado.");
        }
        return encontrado.get();
    }

    public void validarAcessoAsSolicitacoes(Long usuarioIdSolicitado, String emailLogado) {
        Usuario logado = buscarPorEmail(emailLogado);
        if (logado.getPerfil() == PerfilUsuario.GESTOR) {
            return;
        }
        if (!logado.getId().equals(usuarioIdSolicitado)) {
            throw new ValidacaoException("Você só pode ver suas próprias solicitações.");
        }
    }

    private Authentication validarCredenciais(String email, String senha) {
        try {
            return gerenciadorAutenticacao.authenticate(
                    new UsernamePasswordAuthenticationToken(email, senha)
            );
        } catch (BadCredentialsException ex) {
            throw new ValidacaoException("E-mail ou senha inválidos.");
        } catch (DisabledException ex) {
            throw new ValidacaoException("Usuário Inativo.");
        }
    }

    private void salvarSessao(Authentication autenticacao,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(autenticacao);
        SecurityContextHolder.setContext(contexto);
        repositorioContexto.saveContext(contexto, request, response);
    }

    private void validarTextoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacaoException(mensagem);
        }
    }
}
