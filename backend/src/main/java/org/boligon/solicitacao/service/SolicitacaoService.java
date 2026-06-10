package org.boligon.solicitacao.service;

import org.boligon.auth.domain.Usuario;
import org.boligon.auth.service.AuthService;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.exception.EntidadeNaoEncontradaException;
import org.boligon.exception.ValidacaoException;
import org.boligon.historico.dto.HistoricoStatusDTO;
import org.boligon.historico.service.HistoricoStatusService;
import org.boligon.shared.model.FilaAtendimento;
import org.boligon.solicitacao.domain.Auditoria;
import org.boligon.solicitacao.domain.Solicitacao;
import org.boligon.solicitacao.dto.CriarSolicitacaoRequest;
import org.boligon.solicitacao.dto.FiltroSolicitacaoDTO;
import org.boligon.solicitacao.repository.AuditoriaRepository;
import org.boligon.solicitacao.repository.SolicitacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final HistoricoStatusService historicoStatusService;
    private final AuditoriaRepository auditoriaRepository;
    private final AuthService authService;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,
                              HistoricoStatusService historicoStatusService,
                              AuditoriaRepository auditoriaRepository,
                              AuthService authService) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.historicoStatusService = historicoStatusService;
        this.auditoriaRepository = auditoriaRepository;
        this.authService = authService;
    }

    @Transactional
    public Solicitacao criarSolicitacao(CriarSolicitacaoRequest request) {
        Solicitacao novaSolicitacao = montarSolicitacao(request);
        Usuario usuarioLogado = resolverUsuario(request);
        validarSolicitacao(novaSolicitacao);
        definirAutorSolicitacao(novaSolicitacao, usuarioLogado, request.isAnonima());

        novaSolicitacao.setProtocolo(gerarProtocolo());
        novaSolicitacao.setDataCriacao(LocalDateTime.now());
        novaSolicitacao.setStatus(StatusSolicitacao.ABERTO);
        novaSolicitacao.setPrazoSla(calcularPrazoSla(novaSolicitacao.getPrioridade()));

        Solicitacao salva = solicitacaoRepository.save(novaSolicitacao);

        registrarAuditoria(salva.getUsuario(), "CRIAR_SOLICITACAO", "Protocolo " + salva.getProtocolo());

        return salva;
    }

    public Solicitacao buscarPorProtocolo(String protocolo) {
        if (protocolo == null || protocolo.trim().isEmpty()) {
            throw new ValidacaoException("É obrigatório informar um protocolo nessa busca.");
        }

        Optional<Solicitacao> encontrada = solicitacaoRepository.findByProtocolo(protocolo.trim());
        if (encontrada.isEmpty()) {
            throw new ValidacaoException("Solicitação não encontrada.");
        }
        return encontrada.get();
    }

    public List<Solicitacao> listarPorUsuarioId(Long id) {
        if (id == null) {
            throw new ValidacaoException("É obrigatório informar o id do Usuário");
        }
        return solicitacaoRepository.findByUsuario_IdOrderByDataCriacaoDesc(id);
    }

    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.findAllByOrderByDataCriacaoDesc();
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

    @Transactional
    public void atualizarStatus(HistoricoStatusDTO dto) {
        historicoStatusService.validarAtualizacaoStatus(dto);

        Optional<Solicitacao> encontrada = solicitacaoRepository.findById(dto.getSolicitacaoId());
        if (encontrada.isEmpty()) {
            throw new EntidadeNaoEncontradaException("Solicitação não encontrada.");
        }
        Solicitacao solicitacao = encontrada.get();

        solicitacao.getStatus().validarTransicaoPara(dto.getStatusNovo());

        StatusSolicitacao statusAnterior = solicitacao.getStatus();

        boolean atrasado = LocalDateTime.now().isAfter(solicitacao.getPrazoSla());
        String justificativaFinal = solicitacao.getJustificativaAtraso();
        if (atrasado) {
            if (dto.getJustificativaAtraso() == null || dto.getJustificativaAtraso().trim().isEmpty()) {
                throw new ValidacaoException("Prazo do SLA foi ultrapassado. Informe a justificativa do atraso.");
            }
            justificativaFinal = dto.getJustificativaAtraso().trim();
        }

        solicitacao.setStatus(dto.getStatusNovo());
        solicitacao.setJustificativaAtraso(justificativaFinal);
        solicitacaoRepository.save(solicitacao);

        historicoStatusService.registrarMovimentacao(dto, statusAnterior);

        Usuario responsavel = authService.buscarPorId(dto.getResponsavelId());
        registrarAuditoria(
                responsavel,
                "ATUALIZAR_STATUS",
                "Protocolo " + solicitacao.getProtocolo() + ": " + statusAnterior + " -> " + dto.getStatusNovo()
        );
    }

    private void registrarAuditoria(Usuario usuario, String tipoAcao, String detalhe) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setTipoAcao(tipoAcao);
        auditoria.setDetalhe(truncar(detalhe, 500));
        auditoria.setDataRegistro(LocalDateTime.now());
        auditoriaRepository.save(auditoria);
    }

    private static String truncar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= max) {
            return texto;
        }
        return texto.substring(0, max);
    }

    private Solicitacao montarSolicitacao(CriarSolicitacaoRequest request) {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setCategoria(request.getCategoria());
        solicitacao.setDescricao(request.getDescricao());
        solicitacao.setAnexo(request.getAnexo());
        solicitacao.setLocalizacao(request.getLocalizacao());
        solicitacao.setBairro(request.getBairro());
        solicitacao.setPrioridade(request.getPrioridade());
        solicitacao.setAnonima(request.isAnonima());
        return solicitacao;
    }

    private Usuario resolverUsuario(CriarSolicitacaoRequest request) {
        if (request.getUsuarioId() == null) {
            return null;
        }
        return authService.buscarPorId(request.getUsuarioId());
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

        solicitacao.setLocalizacao(solicitacao.getLocalizacao().trim());
        solicitacao.setBairro(solicitacao.getBairro().trim());
    }

    private void definirAutorSolicitacao(Solicitacao solicitacao, Usuario usuarioLogado, boolean anonima) {
        if (anonima) {
            solicitacao.setAnonima(true);
            solicitacao.setUsuario(null);
            return;
        }

        if (usuarioLogado == null) {
            throw new ValidacaoException("Solicitação identificada exige usuário autenticado com cadastro.");
        }

        solicitacao.setAnonima(false);
        solicitacao.setUsuario(usuarioLogado);
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

}
