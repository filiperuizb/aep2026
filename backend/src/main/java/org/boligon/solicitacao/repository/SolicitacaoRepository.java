package org.boligon.solicitacao.repository;

import org.boligon.solicitacao.domain.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    Optional<Solicitacao> findByProtocolo(String protocolo);

    List<Solicitacao> findByUsuario_IdOrderByDataCriacaoDesc(Long usuarioId);

    List<Solicitacao> findAllByOrderByDataCriacaoDesc();
}
