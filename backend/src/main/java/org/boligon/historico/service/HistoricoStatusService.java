package org.boligon.historico.service;

import org.boligon.auth.domain.Usuario;
import org.boligon.auth.service.AuthService;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.exception.ValidacaoException;
import org.boligon.historico.domain.HistoricoStatus;
import org.boligon.historico.dto.HistoricoStatusDTO;
import org.boligon.historico.repository.HistoricoStatusRepository;
import org.boligon.solicitacao.domain.Solicitacao;
import org.boligon.solicitacao.repository.SolicitacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricoStatusService {

    private final HistoricoStatusRepository historicoStatusRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final AuthService authService;

    public HistoricoStatusService(HistoricoStatusRepository historicoStatusRepository,
                                  SolicitacaoRepository solicitacaoRepository,
                                  AuthService authService) {
        this.historicoStatusRepository = historicoStatusRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.authService = authService;
    }

    public void registrarMovimentacao(HistoricoStatusDTO dto, StatusSolicitacao statusAnterior) {
        validarAtualizacaoStatus(dto);

        Optional<Solicitacao> encontrada = solicitacaoRepository.findById(dto.getSolicitacaoId());
        if (encontrada.isEmpty()) {
            throw new ValidacaoException("Solicitação não encontrada.");
        }
        Solicitacao solicitacao = encontrada.get();
        Usuario responsavel = authService.buscarPorId(dto.getResponsavelId());

        HistoricoStatus historicoStatus = new HistoricoStatus();
        historicoStatus.setSolicitacao(solicitacao);
        historicoStatus.setStatusAnterior(statusAnterior);
        historicoStatus.setStatusNovo(dto.getStatusNovo());
        historicoStatus.setComentario(dto.getComentario().trim());
        historicoStatus.setResponsavel(responsavel);
        historicoStatus.setDataMovimentacao(LocalDateTime.now());

        historicoStatusRepository.save(historicoStatus);
    }

    public List<HistoricoStatus> listarPorSolicitacaoId(Long solicitacaoId) {
        if (solicitacaoId == null) {
            throw new ValidacaoException("A solicitação é obrigatória.");
        }
        return historicoStatusRepository.findBySolicitacao_IdOrderByDataMovimentacaoDesc(solicitacaoId);
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
