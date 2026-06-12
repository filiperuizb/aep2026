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
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitacaoService {

    private static final int TAMANHO_MAXIMO_ANEXO = 5 * 1024 * 1024;

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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<Solicitacao> listarPorUsuarioId(Long id) {
        if (id == null) {
            throw new ValidacaoException("É obrigatório informar o id do Usuário");
        }
        return solicitacaoRepository.findByUsuario_IdOrderByDataCriacaoDesc(id);
    }

    @Transactional(readOnly = true)
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
        String bairroFiltro = "";
        if (filtro.getBairro() != null) {
            bairroFiltro = filtro.getBairro().trim().toLowerCase();
        }

        List<Solicitacao> resultado = new ArrayList<>();
        for (Solicitacao solicitacao : listarTodas()) {
            if (passaNoFiltro(solicitacao, filtro, bairroFiltro)) {
                resultado.add(solicitacao);
            }
        }
        return resultado;
    }

    private boolean passaNoFiltro(Solicitacao solicitacao, FiltroSolicitacaoDTO filtro, String bairroFiltro) {
        if (filtro.getPrioridade() != null && solicitacao.getPrioridade() != filtro.getPrioridade()) {
            return false;
        }
        if (filtro.getCategoria() != null && solicitacao.getCategoria() != filtro.getCategoria()) {
            return false;
        }
        if (filtro.getStatus() != null && solicitacao.getStatus() != filtro.getStatus()) {
            return false;
        }
        if (!bairroFiltro.isEmpty()) {
            if (solicitacao.getBairro() == null) {
                return false;
            }
            if (!solicitacao.getBairro().toLowerCase().contains(bairroFiltro)) {
                return false;
            }
        }
        return true;
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
        aplicarAnexo(solicitacao, request);
        solicitacao.setLocalizacao(request.getLocalizacao());
        solicitacao.setBairro(request.getBairro());
        solicitacao.setPrioridade(request.getPrioridade());
        solicitacao.setAnonima(request.isAnonima());
        return solicitacao;
    }

    private void aplicarAnexo(Solicitacao solicitacao, CriarSolicitacaoRequest request) {
        if (request.getAnexoBase64() == null || request.getAnexoBase64().isBlank()) {
            return;
        }

        String base64 = request.getAnexoBase64().trim();
        int separador = base64.indexOf(',');
        if (separador >= 0) {
            base64 = base64.substring(separador + 1);
        }

        byte[] dados;
        try {
            dados = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException ex) {
            throw new ValidacaoException("O anexo enviado é inválido.");
        }

        solicitacao.setAnexoDados(dados);
        solicitacao.setAnexoNome(
                request.getAnexoNome() != null && !request.getAnexoNome().isBlank()
                        ? request.getAnexoNome().trim()
                        : "anexo"
        );
        solicitacao.setAnexoTipo(
                request.getAnexoTipo() != null && !request.getAnexoTipo().isBlank()
                        ? request.getAnexoTipo().trim()
                        : "application/octet-stream"
        );
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

        if (solicitacao.possuiAnexo()) {
            if (solicitacao.getAnexoNome() == null || solicitacao.getAnexoNome().isBlank()) {
                throw new ValidacaoException("O nome do anexo é obrigatório quando um arquivo é enviado.");
            }
            if (solicitacao.getAnexoNome().length() > 255) {
                throw new ValidacaoException("O nome do anexo não pode ultrapassar 255 caracteres.");
            }
            if (solicitacao.getAnexoDados().length > TAMANHO_MAXIMO_ANEXO) {
                throw new ValidacaoException("O anexo não pode ultrapassar 5 MB.");
            }
            solicitacao.setAnexoNome(solicitacao.getAnexoNome().trim());
        } else {
            solicitacao.setAnexoNome(null);
            solicitacao.setAnexoTipo(null);
            solicitacao.setAnexoDados(null);
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
