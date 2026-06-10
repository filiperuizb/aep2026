package org.boligon.solicitacao.repository;

import org.boligon.solicitacao.domain.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
}
