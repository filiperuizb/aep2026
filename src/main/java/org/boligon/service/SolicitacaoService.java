package org.boligon.service;

import org.boligon.dto.FiltroSolicitacaoDTO;
import org.boligon.dto.HistoricoStatusDTO;
import org.boligon.entity.Solicitacao;
import org.boligon.entity.Usuario;
import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.exception.EntidadeNaoEncontradaException;
import org.boligon.exception.ValidacaoException;
import org.boligon.model.FilaAtendimento;
import org.boligon.repository.AuditoriaRepository;
import org.boligon.repository.SolicitacaoRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository = new SolicitacaoRepository();
    private final HistoricoStatusService historicoStatusService = new HistoricoStatusService();
    private final AuditoriaRepository auditoriaRepository = new AuditoriaRepository();

    public void criarSolicitacao(Solicitacao novaSolicitacao, Usuario usuarioLogado) {
        validarSolicitacao(novaSolicitacao);
        definirAutorSolicitacao(novaSolicitacao, usuarioLogado);

        novaSolicitacao.setProtocolo(gerarProtocolo());
        novaSolicitacao.setDataCriacao(LocalDateTime.now());
        novaSolicitacao.setStatus(StatusSolicitacao.ABERTO);
        novaSolicitacao.setPrazoSla(calcularPrazoSla(novaSolicitacao.getPrioridade()));

        solicitacaoRepository.salvar(novaSolicitacao);
    }

    public Solicitacao buscarPorProtocolo(String protocolo) {
        if (protocolo == null || protocolo.trim().isEmpty()) {
            throw new ValidacaoException("É obrigatório informar um protocolo nessa busca.");
        }

        Solicitacao solicitacao = solicitacaoRepository.buscarPorProtocolo(protocolo.trim());

        if (solicitacao == null) {
            throw new ValidacaoException("Solicitação não encontrada.");
        }
        return solicitacao;
    }

    public List<Solicitacao> listarPorUsuarioId(Long id) {
        if (id == null) {
            throw new ValidacaoException("É obrigatório informar o id do Usuário");
        }
        return solicitacaoRepository.listarPorUsuarioId(id);
    }

    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.listarTodas();
    }

    public FilaAtendimento obterFilaDeAtendimento() {
        return new FilaAtendimento(listarTodas());
    }

    public List<Solicitacao> filtrar(FiltroSolicitacaoDTO filtro) {
        if (filtro == null || filtro.isVazio()) {
            throw new ValidacaoException("Informe ao menos um critério de filtro.");
        }
        List<Solicitacao> todas = listarTodas();
        List<Solicitacao> resultado = new ArrayList<>();
        String bairroFiltro = filtro.getBairro() != null ? filtro.getBairro().trim().toLowerCase() : "";

        for (Solicitacao s : todas) {
            if (filtro.getPrioridade() != null && s.getPrioridade() != filtro.getPrioridade()) {
                continue;
            }
            if (filtro.getCategoria() != null && s.getCategoria() != filtro.getCategoria()) {
                continue;
            }
            if (filtro.getStatus() != null && s.getStatus() != filtro.getStatus()) {
                continue;
            }
            if (!bairroFiltro.isEmpty()) {
                if (s.getBairro() == null || !s.getBairro().toLowerCase().contains(bairroFiltro)) {
                    continue;
                }
            }
            resultado.add(s);
        }
        return resultado;
    }

    private void validarSolicitacao(Solicitacao solicitacao) {

        if (solicitacao == null) {
            throw new ValidacaoException("A solicitação é obrigatória.");
        }

        if (solicitacao.getCategoria() == null) {
            throw new ValidacaoException("A categoria é obrigatória.");
        }

        if (solicitacao.getDescricao() == null || solicitacao.getDescricao().trim().isEmpty()) {
            throw new ValidacaoException("A descrição é obrigatória.");
        }

        String descricao = solicitacao.getDescricao().trim();
        if (descricao.length() < 15) {
            throw new ValidacaoException("A descrição deve ter pelo menos 15 caracteres.");
        }
        if (descricao.length() > 1000) {
            throw new ValidacaoException("A descrição não pode ultrapassar 1000 caracteres.");
        }
        solicitacao.setDescricao(descricao);

        if (solicitacao.getAnexo() != null && !solicitacao.getAnexo().isBlank()) {
            String anexo = solicitacao.getAnexo().trim();
            if (anexo.length() > 255) {
                throw new ValidacaoException("O caminho ou nome do anexo não pode ultrapassar 255 caracteres.");
            }
            solicitacao.setAnexo(anexo);
        } else {
            solicitacao.setAnexo(null);
        }

        if (solicitacao.getLocalizacao() == null || solicitacao.getLocalizacao().trim().isEmpty()) {
            throw new ValidacaoException("A localização é obrigatória.");
        }

        if (solicitacao.getBairro() == null || solicitacao.getBairro().trim().isEmpty()) {
            throw new ValidacaoException("O bairro é obrigatório.");
        }

        if (solicitacao.getPrioridade() == null) {
            throw new ValidacaoException("A prioridade é obrigatória.");
        }
    }

    private void definirAutorSolicitacao(Solicitacao solicitacao, Usuario usuarioLogado) {
        if (solicitacao.isAnonima()) {
            solicitacao.setUsuarioId(null);
            return;
        }

        if (usuarioLogado.getId() == null) {
            throw new ValidacaoException("Solicitação identificada exige usuário autenticado com cadastro.");
        }

        solicitacao.setUsuarioId(usuarioLogado.getId());
    }

    private String gerarProtocolo() {
        String data = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long numero = System.currentTimeMillis() % 100000;

        return "OBS - " + data + "-" + numero;
    }

    private LocalDateTime calcularPrazoSla(Prioridade prioridade) {
        return LocalDateTime.now().plusDays(prioridade.getDiasSla());
    }

    public void atualizarStatus(HistoricoStatusDTO dto) {

        this.historicoStatusService.validarAtualizacaoStatus(dto);

        Solicitacao solicitacao = solicitacaoRepository.buscarPorId(dto.getSolicitacaoId());

        if (solicitacao == null) {
            throw new EntidadeNaoEncontradaException("Solicitação não encontrada.");
        }

        validarTransicaoDeStatus(solicitacao.getStatus(), dto.getStatusNovo());

        StatusSolicitacao statusAnterior = solicitacao.getStatus();

        boolean atrasado = LocalDateTime.now().isAfter(solicitacao.getPrazoSla());
        String justificativaFinal = solicitacao.getJustificativaAtraso();
        if (atrasado) {
            if (dto.getJustificativaAtraso() == null || dto.getJustificativaAtraso().trim().isEmpty()) {
                throw new ValidacaoException("Prazo do SLA foi ultrapassado. Informe a justificativa do atraso.");
            }
            justificativaFinal = dto.getJustificativaAtraso().trim();
        }

        solicitacaoRepository.atualizarStatus(
                dto.getSolicitacaoId(),
                dto.getStatusNovo(),
                justificativaFinal
        );

        historicoStatusService.registrarMovimentacao(
                dto,
                statusAnterior
        );

        auditoriaRepository.registrar(
                dto.getResponsavelId(),
                "ATUALIZAR_STATUS",
                "Protocolo " + solicitacao.getProtocolo() + ": " + statusAnterior + " -> " + dto.getStatusNovo()
        );
    }

    private void validarTransicaoDeStatus(StatusSolicitacao atual, StatusSolicitacao novo) {
        if (atual == novo) {
            throw new ValidacaoException("A solicitação já está com este status.");
        }

        StatusSolicitacao esperado = switch (atual) {
            case ABERTO -> StatusSolicitacao.TRIAGEM;
            case TRIAGEM -> StatusSolicitacao.EM_EXECUCAO;
            case EM_EXECUCAO -> StatusSolicitacao.RESOLVIDO;
            case RESOLVIDO -> StatusSolicitacao.ENCERRADO;
            case ENCERRADO -> null;
        };

        if (esperado == null) {
            throw new ValidacaoException("Não é possível alterar o status de uma solicitação encerrada.");
        }

        if (novo != esperado) {
            throw new ValidacaoException("Transição inválida: de " + atual + " só é permitido ir para " + esperado + ".");
        }
    }

    public List<Solicitacao> listarPorPrioridade(Prioridade prioridade) {
        if (prioridade == null) {
            throw new ValidacaoException("A prioridade é obrigatória.");
        }
        return solicitacaoRepository.listarPorPrioridade(prioridade);
    }

    public List<Solicitacao> listarPorStatus(StatusSolicitacao status) {
        if (status == null) {
            throw new ValidacaoException("O status é obrigatório.");
        }
        return solicitacaoRepository.listarPorStatus(status);
    }

    public List<Solicitacao> listarPorBairro(String bairro) {
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new ValidacaoException("O bairro é obrigatório.");
        }
        return solicitacaoRepository.listarPorBairro(bairro.trim());
    }

    public List<Solicitacao> listarPorCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new ValidacaoException("A categoria é obrigatória.");
        }
        return solicitacaoRepository.listarPorCategoria(categoria);
    }
}
