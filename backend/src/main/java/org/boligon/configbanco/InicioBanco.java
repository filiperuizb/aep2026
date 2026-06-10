package org.boligon.configbanco;

import org.boligon.auth.domain.Usuario;
import org.boligon.auth.repository.UsuarioRepository;
import org.boligon.enums.PerfilUsuario;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class InicioBanco {

    private final UsuarioRepository usuarioRepository;

    public InicioBanco(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostConstruct
    public void inicializarBanco() {
        garantirGestorPadrao();
        garantirCidadaoPadrao();
    }

    private void garantirGestorPadrao() {
        if (!usuarioRepository.existsByEmail("admin@admin.com")) {
            Usuario gestor = new Usuario();
            gestor.setNome("Administrador Teste");
            gestor.setEmail("admin@admin.com");
            gestor.setSenha("123");
            gestor.setPerfil(PerfilUsuario.GESTOR);
            gestor.setAtivo(true);
            usuarioRepository.save(gestor);
        }
    }

    private void garantirCidadaoPadrao() {
        if (!usuarioRepository.existsByEmail("cidadao@test.com")) {
            Usuario cidadao = new Usuario();
            cidadao.setNome("Cidadão Teste");
            cidadao.setEmail("cidadao@test.com");
            cidadao.setSenha("123");
            cidadao.setPerfil(PerfilUsuario.CIDADAO);
            cidadao.setAtivo(true);
            usuarioRepository.save(cidadao);
        }
    }
}
