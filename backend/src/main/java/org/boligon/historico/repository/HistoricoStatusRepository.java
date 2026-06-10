package org.boligon.historico.repository;

import org.boligon.historico.domain.HistoricoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoStatusRepository extends JpaRepository<HistoricoStatus, Long> {

    List<HistoricoStatus> findBySolicitacao_IdOrderByDataMovimentacaoDesc(Long solicitacaoId);
}
