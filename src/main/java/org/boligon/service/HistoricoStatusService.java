package org.boligon.service;

import org.boligon.dto.HistoricoStatusDTO;
import org.boligon.entity.HistoricoStatus;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.exception.ValidacaoException;
import org.boligon.repository.HistoricoStatusRepository;
import org.boligon.repository.SolicitacaoRepository;

import java.time.LocalDateTime;
import java.util.List;

public class HistoricoStatusService {

    private final HistoricoStatusRepository historicoStatusRepository = new HistoricoStatusRepository();

    public void registrarMovimentacao(HistoricoStatusDTO dto,
                                      StatusSolicitacao statusAnterior) {

        validarAtualizacaoStatus(dto);

        HistoricoStatus historicoStatus = new HistoricoStatus();
        historicoStatus.setSolicitacaoId(dto.getSolicitacaoId());
        historicoStatus.setStatusAnterior(statusAnterior);
        historicoStatus.setStatusNovo(dto.getStatusNovo());
        historicoStatus.setComentario(dto.getComentario().trim());
        historicoStatus.setResponsavelId(dto.getResponsavelId());
        historicoStatus.setDataMovimentacao(LocalDateTime.now());

        historicoStatusRepository.salvar(historicoStatus);
    }

    public List<HistoricoStatus> listarPorSolicitacaoId(Long solicitacaoId) {

        if (solicitacaoId == null) {
            throw new ValidacaoException("A solicitação é obrigatória.");
        }

        return historicoStatusRepository.listarPorSolicitacaoId(solicitacaoId);
    }

    public void validarAtualizacaoStatus(HistoricoStatusDTO dto) {

        if (dto == null) {
            throw new ValidacaoException("Dados da atualização são obrigatórios.");
        }

        if (dto.getSolicitacaoId() == null) {
            throw new ValidacaoException("A solicitação é obrigatória.");
        }

        if (dto.getStatusNovo() == null) {
            throw new ValidacaoException("O novo status é obrigatório.");
        }

        if (dto.getComentario() == null || dto.getComentario().trim().isEmpty()) {
            throw new ValidacaoException("O comentário é obrigatório.");
        }

        if (dto.getResponsavelId() == null) {
            throw new ValidacaoException("O responsável é obrigatório.");
        }
    }
}